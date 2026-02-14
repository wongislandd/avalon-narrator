package com.avalonnarrator.playback

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultClipResolverTest {

    private val resolver = DefaultClipResolver()

    @Test
    fun `resolves clip from selected voice pack`() {
        val resolution = resolver.resolve(ClipId.INTRO, VoicePackId.DRAMATIC_EN)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackId.DRAMATIC_EN, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }

    @Test
    fun `resolves lady of the lake from dramatic pack when available`() {
        val resolution = resolver.resolve(ClipId.LADY_OF_LAKE, VoicePackId.DRAMATIC_EN)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackId.DRAMATIC_EN, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }

    @Test
    fun `resolves clip from rainbird pack when available`() {
        val resolution = resolver.resolve(ClipId.INTRO, VoicePackId.RAINBIRD_EN)
        val found = assertIs<ClipResolution.Found>(resolution)
        assertEquals(VoicePackId.RAINBIRD_EN, found.clip.sourceVoicePack)
        assertTrue(!found.clip.usedFallback)
    }
}
