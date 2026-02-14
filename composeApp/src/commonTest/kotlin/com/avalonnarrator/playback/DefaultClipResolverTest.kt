package com.avalonnarrator.playback

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultClipResolverTest {

    private val resolver = DefaultClipResolver()

    @Test
    fun `resolves clip from selected voice pack`() {
        val resolution = resolver.resolve(ClipId.INTRO, VoicePackIds.WIZARD)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackIds.WIZARD, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }

    @Test
    fun `resolves lady of the lake from wizard pack when available`() {
        val resolution = resolver.resolve(ClipId.LADY_OF_LAKE, VoicePackIds.WIZARD)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackIds.WIZARD, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }

    @Test
    fun `resolves clip from rainbird pack when available`() {
        val resolution = resolver.resolve(ClipId.INTRO, VoicePackIds.RAINBIRD_EN)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackIds.RAINBIRD_EN, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }
}
