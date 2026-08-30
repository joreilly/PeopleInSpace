// Shims for the generated Interop.cs from kotlin-native-nuget 0.3.0. Both are workarounds for
// generator bugs and go away once they are fixed upstream; see LIMITATIONS.md.

// xxfast/kotlin-native-nuget#41: types in the root namespace refer to types from other exported
// packages by bare name in property, constructor and list-element positions. Global aliases make
// those names resolve without editing the generated file.
global using Assignment = PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Remote.Assignment;
global using IssPosition = PeopleInSpace.Kotlin.Dev.Johnoreilly.Common.Remote.IssPosition;

namespace PeopleInSpace.Kotlin;

/// <summary>
/// xxfast/kotlin-native-nuget#42: exported classes that implement an interface from a library
/// outside the export set (here Koin's <c>KoinComponent</c>) are emitted as implementing an
/// <c>IKoinComponent</c> that is never generated. This empty marker satisfies the reference.
/// </summary>
public interface IKoinComponent;
