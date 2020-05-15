//
//  AppDelegate.swift
//  PeopleInSpace
//
//  Created by John O'Reilly on 04/02/2020.
//  Copyright © 2020 John O'Reilly. All rights reserved.
//

import Cocoa
import SwiftUI
import common

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {

    var window: NSWindow!


    func applicationDidFinishLaunching(_ aNotification: Notification) {
        KoinKt.doInitKoin()
        
        // Create the SwiftUI view that provides the window contents.
        let contentView = ContentView()
        
        // Create the window and set the content view. 
        window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 480, height: 300),
            styleMask: [.titled, .closable, .miniaturizable, .resizable, .fullSizeContentView],
            backing: .buffered, defer: false)
        window.center()
        window.setFrameAutosaveName("Main Window")
        window.contentView = NSHostingView(rootView: contentView)
        window.makeKeyAndOrderFront(nil)
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        // Insert code here to tear down your application
    }


}

