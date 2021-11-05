import WidgetKit
import SwiftUI
import Combine
import common

final class Provider: TimelineProvider {
    var timelineCancellable: AnyCancellable?

    init() {
        KoinKt.doInitKoin()
    }

    private var entryPublisher: AnyPublisher<PeopleInSpaceListEntry, Never> {

        let future = Future<PeopleInSpaceListEntry, Never> { promise in
            let repository =  PeopleInSpaceRepository()

            repository.startObservingPeopleUpdates(success: { data in
                promise(.success(PeopleInSpaceListEntry(date: Date(), peopleList: data)))
            })
        }
        return AnyPublisher(future)
    }

    func placeholder(in context: Context) -> PeopleInSpaceListEntry {
        PeopleInSpaceListEntry(date: Date(), peopleList: [])
    }

    func getSnapshot(in context: Context, completion: @escaping (PeopleInSpaceListEntry) -> ()) {
        let entry = PeopleInSpaceListEntry(date: Date(), peopleList: [])
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        timelineCancellable = entryPublisher
            .map { Timeline(entries: [$0], policy: .atEnd) }
            .receive(on: DispatchQueue.main)
            .sink(receiveValue: completion)
    }
}


struct PeopleInSpaceListEntry: TimelineEntry {
    let date: Date
    let peopleList: [Assignment]?
}


struct PeopleInSpaceWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        VStack {
            if let peopleList = entry.peopleList {
                ForEach(peopleList, id:\.name) { person in
                    Text("\(person.name) (\(person.craft))").font(.body)
                }
            }
        }
    }
}

@main
struct PeopleInSpaceWidget: Widget {
    let kind: String = "PeopleInSpaceWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            PeopleInSpaceWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("My Widget")
        .description("This is an example widget.")
        .supportedFamilies([.systemLarge])
    }
}

