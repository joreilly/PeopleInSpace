// Shim for the generated Interop.cs from kotlin-native-nuget 0.4.0; goes away once the generator
// fix ships (see LIMITATIONS.md).
//
// xxfast/kotlin-native-nuget#50 (follow-up to #41): properties on a sealed subclass still refer to types
// from another exported package by bare name (`IReadOnlyList<Assignment> Result`,
// `IssPosition Position` inside the Viewmodel namespace), while top-level classes are qualified
// with `global::`. Global aliases make those names resolve without editing the generated file.
global using Assignment = PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Remote.Assignment;
global using IssPosition = PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Remote.IssPosition;
