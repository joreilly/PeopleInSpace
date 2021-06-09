package com.surrus.common.repository

import com.rickclephas.kmp.nativecoroutines.nativeSuspend

fun PeopleInSpaceRepository.fetchPeopleNative(delayMs: Long)
        = nativeSuspend { fetchPeople(delayMs) }
