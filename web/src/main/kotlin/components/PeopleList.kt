package components

import components.materialui.Avatar
import components.materialui.ListItem
import components.materialui.ListItemAvatar
import components.materialui.ListItemText
import model.Person
import react.RBuilder

fun RBuilder.PeopleList(
    people: List<Person>,
    selectedPerson: Person?,
    onSelect: (Person) -> Unit
) {
    components.materialui.List {
        people.forEach { item ->
            ListItem {
                attrs {
                    button = true
                    key = item.assignment.name
                    selected = item == selectedPerson
                    onClick = {
                        onSelect(item)
                    }
                }
                ListItemAvatar {
                    Avatar {
                        attrs.src = item.imageUrl
                    }
                }
                ListItemText {
                    +"${item.assignment.name} (${item.assignment.craft})"
                }
            }
        }
    }
}