import WidgetKit
import SwiftUI
import Combine
import common
import KMPNativeCoroutinesCombine

final class Provider: TimelineProvider {
    
    private let repository: PeopleInSpaceRepository
    private var timelineCancellable: AnyCancellable?

    init() {
        KoinKt.doInitKoin()
        repository = PeopleInSpaceRepository()
    }

    func placeholder(in context: Context) -> PeopleInSpaceListEntry {
        PeopleInSpaceListEntry(date: Date(), peopleList: [])
    }

    func getSnapshot(in context: Context, completion: @escaping (PeopleInSpaceListEntry) -> ()) {
        let entry = PeopleInSpaceListEntry(date: Date(), peopleList: [])
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        timelineCancellable = createFuture(for: repository.fetchPeople())
            .map { data in
                let entry = PeopleInSpaceListEntry(date: Date(), peopleList: data)
                return Timeline(entries: [entry], policy: .atEnd)
            }
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                debugPrint(completion)
            }, receiveValue: completion)
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

