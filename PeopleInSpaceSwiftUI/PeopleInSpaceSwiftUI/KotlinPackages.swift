import Common

// Short names for :common's Kotlin packages. The new `export { swift { rootPackage } }` DSL only
// flattens the exporting (umbrella) module's own packages, so :common's aren't aliased for us.
typealias di = ExportedKotlinPackages.dev.johnoreilly.common.di
typealias remote = ExportedKotlinPackages.dev.johnoreilly.common.remote
typealias repository = ExportedKotlinPackages.dev.johnoreilly.common.repository
typealias ui = ExportedKotlinPackages.dev.johnoreilly.common.ui
typealias viewmodel = ExportedKotlinPackages.dev.johnoreilly.common.viewmodel
