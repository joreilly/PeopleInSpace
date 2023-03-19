// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "PeopleInSpaceKit",
    platforms: [
        .iOS(.v14)
    ],
    products: [
        .library(
            name: "PeopleInSpaceKit",
            targets: ["PeopleInSpaceKit"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "PeopleInSpaceKit",
            path: "./PeopleInSpaceKit.xcframework"
        ),
    ]
)
