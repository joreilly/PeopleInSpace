// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "common",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "common",
            targets: ["common"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "common",
            path: "./common.xcframework"
        ),
    ]
)
