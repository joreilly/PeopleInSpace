package components

import com.surrus.common.remote.Assignment
import components.materialui.Avatar
import components.materialui.ListItem
import components.materialui.ListItemAvatar
import components.materialui.ListItemText
import react.RBuilder

fun RBuilder.PeopleList(
    people: List<Assignment>,
    selectedPerson: Assignment?,
    onSelect: (Assignment) -> Unit
) {
    components.materialui.List {
        people.forEach { item ->
            ListItem {
                attrs {
                    button = true
                    key = item.name
                    selected = item == selectedPerson
                    onClick = {
                        onSelect(item)
                    }
                }
                ListItemAvatar {
                    Avatar {
                        attrs.src = item.personImageUrl
                    }
                }
                ListItemText {
                    +"${item.name} (${item.craft})"
                }
            }
        }
    }
}