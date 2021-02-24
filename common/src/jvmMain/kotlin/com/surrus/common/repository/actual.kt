package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger

actual fun getLogger(): Logger = CommonLogger()