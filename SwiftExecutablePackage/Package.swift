// swift-tools-version:5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "PeopleInSpace",
    platforms: [.macOS(.v13)],
    dependencies: [
        .package(url: "https://github.com/rickclephas/KMP-NativeCoroutines.git", exact: "1.0.0-ALPHA-18"),
        .package(url: "https://github.com/joreilly/PeopleInSpacePackage", branch: "main")
    ],
    targets: [
        .executableTarget(
            name: "peopleinspace",
            dependencies: [
                .product(name: "KMPNativeCoroutinesAsync", package: "KMP-NativeCoroutines"),
                .product(name: "PeopleInSpaceKit", package: "PeopleInSpacePackage")
            ]
        )
    ]
)
