package dev.johnoreilly.common.db

/**
 * File name of the on-device people cache.
 *
 * Renamed from "peopleinspace.db" because #524 briefly dropped the SQLDelight migrations, so
 * existing files are either schema version 3 (pre-#524 installs: "Can't downgrade database from
 * version 3 to 1") or a full schema mislabelled as version 1 (which re-running the migrations
 * would break with duplicate columns). The table is only a cache of the people list, refetched
 * on start, so a fresh file is the safe way out for both.
 */
internal const val DATABASE_NAME = "peopleinspace-v3.db"

/** The pre-rename file, deleted where the platform makes that a one-liner (Android). */
internal const val LEGACY_DATABASE_NAME = "peopleinspace.db"
