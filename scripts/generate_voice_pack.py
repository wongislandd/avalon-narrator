#!/usr/bin/env python3
"""Generate/update Avalon voice packs and regenerate VoicePackCatalog.kt.

Usage examples:
  python3 scripts/generate_voice_pack.py --pack-id storm --display-name "Storm" \
      --description "Sharp dramatic narration" --voice-id <elevenlabs_voice_id>

  python3 scripts/generate_voice_pack.py --sync-only
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
import time
from pathlib import Path
from typing import Dict, List
from urllib import error, request

REPO_ROOT = Path(__file__).resolve().parents[1]
DEFAULT_MANIFEST_PATH = REPO_ROOT / "scripts" / "voice_packs.json"
DEFAULT_CATALOG_PATH = (
    REPO_ROOT
    / "composeApp"
    / "src"
    / "commonMain"
    / "kotlin"
    / "com"
    / "avalonnarrator"
    / "domain"
    / "audio"
    / "VoicePackCatalog.kt"
)
DEFAULT_CLIP_ID_PATH = (
    REPO_ROOT
    / "composeApp"
    / "src"
    / "commonMain"
    / "kotlin"
    / "com"
    / "avalonnarrator"
    / "domain"
    / "audio"
    / "ClipId.kt"
)
DEFAULT_SCRIPT_CATALOG_PATH = (
    REPO_ROOT
    / "composeApp"
    / "src"
    / "commonMain"
    / "kotlin"
    / "com"
    / "avalonnarrator"
    / "domain"
    / "audio"
    / "NarrationScriptCatalog.kt"
)
DEFAULT_AUDIO_ROOT = REPO_ROOT / "composeApp" / "src" / "commonMain" / "resources" / "audio"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate/update voice packs and refresh VoicePackCatalog.kt")
    parser.add_argument("--pack-id", help="Stable voice pack id, e.g. wizard or rainbird_en")
    parser.add_argument("--display-name", help="Human-readable pack name")
    parser.add_argument("--description", default="", help="Pack description shown in selection UI")
    parser.add_argument("--voice-id", help="ElevenLabs voice id used for clip generation")
    parser.add_argument("--asset-dir", help="Audio folder under compose resources/audio (defaults to pack-id)")
    parser.add_argument("--const-name", help="Kotlin constant name for this pack id")
    parser.add_argument("--set-default", action="store_true", help="Set this pack as default in catalog")
    parser.add_argument("--sync-only", action="store_true", help="Only regenerate VoicePackCatalog.kt from manifest")
    parser.add_argument("--skip-audio", action="store_true", help="Skip ElevenLabs clip generation")
    parser.add_argument("--overwrite-audio", action="store_true", help="Regenerate clips even if files already exist")
    parser.add_argument("--manifest", type=Path, default=DEFAULT_MANIFEST_PATH, help="Path to voice_packs.json")
    parser.add_argument("--catalog-file", type=Path, default=DEFAULT_CATALOG_PATH, help="Path to VoicePackCatalog.kt")
    parser.add_argument("--clip-id-file", type=Path, default=DEFAULT_CLIP_ID_PATH, help="Path to ClipId.kt")
    parser.add_argument(
        "--script-catalog-file",
        type=Path,
        default=DEFAULT_SCRIPT_CATALOG_PATH,
        help="Path to NarrationScriptCatalog.kt",
    )
    parser.add_argument("--audio-root", type=Path, default=DEFAULT_AUDIO_ROOT, help="Audio root directory")
    parser.add_argument("--model-id", default="eleven_multilingual_v2", help="ElevenLabs TTS model id")
    parser.add_argument("--stability", type=float, default=0.45, help="ElevenLabs stability [0,1]")
    parser.add_argument("--similarity-boost", type=float, default=0.85, help="ElevenLabs similarity boost [0,1]")
    parser.add_argument("--request-delay", type=float, default=0.0, help="Optional delay between clip requests (seconds)")
    return parser.parse_args()


def read_manifest(path: Path) -> dict:
    if not path.exists():
        return {"default_pack_id": "", "packs": []}
    return json.loads(path.read_text(encoding="utf-8"))


def write_manifest(path: Path, data: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def sanitize_const_name(pack_id: str) -> str:
    sanitized = re.sub(r"[^A-Za-z0-9]+", "_", pack_id).strip("_").upper()
    if not sanitized:
        sanitized = "VOICE_PACK"
    if sanitized[0].isdigit():
        sanitized = f"PACK_{sanitized}"
    return sanitized


def ensure_unique_pack_ids(packs: List[dict]) -> None:
    seen = set()
    for pack in packs:
        pack_id = pack["id"]
        if pack_id in seen:
            raise ValueError(f"Duplicate pack id in manifest: {pack_id}")
        seen.add(pack_id)


def ensure_unique_const_names(packs: List[dict]) -> None:
    seen = set()
    for pack in packs:
        const_name = pack["const_name"]
        if const_name in seen:
            raise ValueError(f"Duplicate const_name in manifest: {const_name}")
        seen.add(const_name)


def upsert_pack(manifest: dict, args: argparse.Namespace) -> dict:
    packs = manifest.get("packs", [])
    pack_id = args.pack_id
    if not pack_id:
        raise ValueError("--pack-id is required unless --sync-only is used")

    existing = next((pack for pack in packs if pack.get("id") == pack_id), None)
    if existing is None:
        if not args.display_name:
            raise ValueError("--display-name is required when creating a new pack")
        existing = {
            "id": pack_id,
            "const_name": args.const_name or sanitize_const_name(pack_id),
            "display_name": args.display_name,
            "description": args.description,
            "asset_dir": args.asset_dir or pack_id,
        }
        if args.voice_id:
            existing["voice_id"] = args.voice_id
        packs.append(existing)
    else:
        if args.const_name:
            existing["const_name"] = args.const_name
        elif "const_name" not in existing or not existing["const_name"]:
            existing["const_name"] = sanitize_const_name(pack_id)
        if args.display_name:
            existing["display_name"] = args.display_name
        if args.description:
            existing["description"] = args.description
        if args.asset_dir:
            existing["asset_dir"] = args.asset_dir
        if args.voice_id:
            existing["voice_id"] = args.voice_id

    if args.set_default:
        manifest["default_pack_id"] = pack_id

    if not manifest.get("default_pack_id"):
        manifest["default_pack_id"] = packs[0]["id"] if packs else ""

    manifest["packs"] = packs
    ensure_unique_pack_ids(packs)
    ensure_unique_const_names(packs)
    return manifest


def kotlin_escape(text: str) -> str:
    return text.replace("\\", "\\\\").replace('"', '\\"')


def render_catalog_kotlin(manifest: dict) -> str:
    packs = manifest["packs"]
    default_pack_id = manifest["default_pack_id"]

    const_by_id: Dict[str, str] = {pack["id"]: pack["const_name"] for pack in packs}
    if default_pack_id not in const_by_id:
        raise ValueError(f"default_pack_id '{default_pack_id}' not found in packs")

    lines: List[str] = []
    lines.append("package com.avalonnarrator.domain.audio")
    lines.append("")
    lines.append("// AUTO-GENERATED by scripts/generate_voice_pack.py. Do not edit manually.")
    lines.append("typealias VoicePackId = String")
    lines.append("")
    lines.append("object VoicePackIds {")
    for pack in packs:
        lines.append(f'    const val {pack["const_name"]}: VoicePackId = "{kotlin_escape(pack["id"])}"')
    lines.append("}")
    lines.append("")
    lines.append("object VoicePackCatalog {")
    lines.append(f"    val defaultPackId: VoicePackId = VoicePackIds.{const_by_id[default_pack_id]}")
    lines.append("")
    lines.append("    private val packs: Map<VoicePackId, VoicePackDefinition> = listOf(")

    for pack in packs:
        asset_dir = pack.get("asset_dir") or pack["id"]
        lines.append("        VoicePackDefinition(")
        lines.append(f"            id = VoicePackIds.{pack['const_name']},")
        lines.append(f'            displayName = "{kotlin_escape(pack["display_name"])}",')
        lines.append(f'            description = "{kotlin_escape(pack.get("description", ""))}",')
        lines.append("            clipFiles = ClipId.entries.associateWith { clip ->")
        lines.append(f'                "audio/{kotlin_escape(asset_dir)}/${{clip.name.lowercase()}}.mp3"')
        lines.append("            },")
        lines.append("        ),")

    lines.append("    ).associateBy { it.id }")
    lines.append("")
    lines.append("    fun all(): List<VoicePackDefinition> = packs.values.toList()")
    lines.append("")
    lines.append("    fun byId(id: VoicePackId): VoicePackDefinition? = packs[id]")
    lines.append("}")
    lines.append("")

    return "\n".join(lines)


def parse_clip_ids(clip_id_file: Path) -> List[str]:
    text = clip_id_file.read_text(encoding="utf-8")
    body_match = re.search(r"enum\\s+class\\s+ClipId\\s*\\{(?P<body>.*?)\\}", text, flags=re.DOTALL)
    if not body_match:
        raise ValueError(f"Could not parse ClipId enum from {clip_id_file}")
    body = body_match.group("body")
    return re.findall(r"^\\s*([A-Z0-9_]+)\\s*,\\s*$", body, flags=re.MULTILINE)


def unescape_kotlin_string(raw: str) -> str:
    return bytes(raw, "utf-8").decode("unicode_escape")


def parse_script_lines(script_catalog_file: Path) -> Dict[str, str]:
    text = script_catalog_file.read_text(encoding="utf-8")
    mapping: Dict[str, str] = {}
    for match in re.finditer(r'ClipId\\.([A-Z0-9_]+)\\s+to\\s+"((?:\\\\.|[^"\\\\])*)"\\s*,', text):
        clip_id = match.group(1)
        line = unescape_kotlin_string(match.group(2))
        mapping[clip_id] = line
    return mapping


def fallback_line(clip_id: str) -> str:
    return " ".join(token.capitalize() for token in clip_id.lower().split("_"))


def elevenlabs_tts(api_key: str, voice_id: str, text: str, model_id: str, stability: float, similarity_boost: float) -> bytes:
    url = f"https://api.elevenlabs.io/v1/text-to-speech/{voice_id}"
    payload = json.dumps(
        {
            "text": text,
            "model_id": model_id,
            "voice_settings": {
                "stability": stability,
                "similarity_boost": similarity_boost,
            },
        }
    ).encode("utf-8")

    req = request.Request(
        url,
        data=payload,
        method="POST",
        headers={
            "xi-api-key": api_key,
            "Content-Type": "application/json",
            "Accept": "audio/mpeg",
        },
    )

    try:
        with request.urlopen(req, timeout=120) as response:
            return response.read()
    except error.HTTPError as exc:
        response_body = exc.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"ElevenLabs request failed ({exc.code}): {response_body}") from exc


def generate_audio_for_pack(pack: dict, args: argparse.Namespace) -> None:
    voice_id = args.voice_id or pack.get("voice_id")
    if not voice_id:
        raise ValueError("No voice id available. Pass --voice-id or set voice_id in manifest for this pack.")

    api_key = os.environ.get("ELEVENLABS_API_KEY")
    if not api_key:
        raise ValueError("ELEVENLABS_API_KEY is required to generate audio clips")

    clip_ids = parse_clip_ids(args.clip_id_file)
    scripted_lines = parse_script_lines(args.script_catalog_file)
    output_dir = args.audio_root / (pack.get("asset_dir") or pack["id"])
    output_dir.mkdir(parents=True, exist_ok=True)

    print(f"Generating {len(clip_ids)} clips into {output_dir}...")

    for index, clip_id in enumerate(clip_ids, start=1):
        output_file = output_dir / f"{clip_id.lower()}.mp3"
        if output_file.exists() and not args.overwrite_audio:
            print(f"[{index}/{len(clip_ids)}] skip {output_file.name} (exists)")
            continue

        line = scripted_lines.get(clip_id, fallback_line(clip_id))
        print(f"[{index}/{len(clip_ids)}] tts {clip_id} -> {output_file.name}")
        audio_bytes = elevenlabs_tts(
            api_key=api_key,
            voice_id=voice_id,
            text=line,
            model_id=args.model_id,
            stability=args.stability,
            similarity_boost=args.similarity_boost,
        )
        output_file.write_bytes(audio_bytes)

        if args.request_delay > 0:
            time.sleep(args.request_delay)


def main() -> int:
    args = parse_args()

    manifest = read_manifest(args.manifest)

    if not args.sync_only:
        manifest = upsert_pack(manifest, args)
        write_manifest(args.manifest, manifest)

    catalog_text = render_catalog_kotlin(manifest)
    args.catalog_file.parent.mkdir(parents=True, exist_ok=True)
    args.catalog_file.write_text(catalog_text, encoding="utf-8")
    print(f"Wrote catalog: {args.catalog_file}")

    if args.sync_only or args.skip_audio:
        return 0

    if not args.pack_id:
        raise ValueError("--pack-id is required when generating audio")

    pack = next((item for item in manifest["packs"] if item["id"] == args.pack_id), None)
    if pack is None:
        raise ValueError(f"Pack id not found in manifest: {args.pack_id}")

    generate_audio_for_pack(pack, args)
    print("Done.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:  # pragma: no cover
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(1)
