package me.dvyy.tasks.layout.ui.components

//@Composable
//fun LayoutTab(
//    tab: LayoutStructure,
//    modifier: Modifier = Modifier.Companion,
//    selected: Boolean,
//    showCloseButton: Boolean = true,
//    onDropLayout: (LayoutStructure) -> Unit = {},
//    onSelect: () -> Unit = {},
//    onClose: () -> Unit = {},
//) {
//    val ui = UI
//    Box(
//        modifier.clickableWithoutRipple { onSelect() }
//            .onMiddleMouseClick { onClose() },
//        /*.dropTarget(
//                    LocalDragAndDropState.current,
//                    shouldStartDragAndDrop = { it.data is Dragged.Layout },
//                ) {
//                    Logger.i { "Dropped ${it.data} on $index" }
//                    onTabbedUpdate(structure.copy(selected = index))
//                }*/
//    ) {
//        DraggableItem(
//            key = remember { Uuid.Companion.random() },
////                        requireFirstDownUnconsumed = true,
//            data = Dragged.Layout(tab),
//            onDragStart = { onClose() },
//            state = LocalDragAndDropState.current
//        ) {
////            FixedEndLayout(
////                Modifier.height(ui.tabHeight),
////                end = {
////                    Row(verticalAlignment = Alignment.CenterVertically) {
////                        Spacer(Modifier.width(UI.padding.sm))
////                        if (showCloseButton) BoxButton(onClick = {
////                            onClose()
////                        }) {
////                            Icon(
////                                TablerIcons.Outlined.X,
////                                "Close tab",
////                                tint = MaterialTheme.colorScheme.onSurfaceVariant
////                            )
////                        }
////                    }
////                }
////            ) {
//            Row(
//                verticalAlignment = Alignment.Companion.CenterVertically,
//                modifier = Modifier.Companion.padding(ui.tabPadding)
//            ) {
//                (tab as? LayoutStructure.Single)?.tabLabel(if (selected) LayoutStructure.Single.Location.Selected else LayoutStructure.Single.Location.TabList)
//                    ?: run {
//                        Text(
//                            "Custom Layout",
//                            maxLines = 1,
//                            overflow = TextOverflow.Companion.Ellipsis,
//                        )
//                        //TODO implement layout saving
////                            val tasksViewModel = koinViewModel<TasksViewModel>()
////                            BoxButton(onClick = { tasksViewModel.saveLayout(tab) }) {
////                                Icon(AppIcons.Save, "Save layout")
////                            }
//                    }
//            }
////            }
//            if (selected) Surface(
//                modifier = Modifier.Companion
//                    .height(UI.size.xsm)
////                    .fillMaxWidth()
//                    .align(Alignment.Companion.BottomCenter),
//                color = if (true/*isActive*/) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
//            ) { }
////            HoverBox(
////                Modifier.fillMaxSize(),
////                onDropped = { new -> onDropLayout(new) }
////            )
//        }
//    }
//}