package me.dvyy.tasks.layout.ui.components

//
//@Composable
//fun TabbedLayout(
//    structure: LayoutStructure.Tabbed,
//    onLayoutUpdate: (LayoutStructure) -> Unit = {},
//) {
//    val layoutViewModel: LayoutViewModel by rememberGlobalViewModel()
////    val topRight by layoutViewModel.topRightLayout.collectAsState()
////    val topRow by layoutViewModel.topRow.collectAsState()
////    val topLeft by layoutViewModel.topLeftLayout.collectAsState()
//
//    val selectable = Modifier.optional(structure.selectable) {
//        pointerInput(structure) {
//            awaitPointerEventScope {
//                while (true) {
//                    awaitFirstDown(pass = PointerEventPass.Initial)
//                    layoutViewModel.setActiveLayout(structure)
//                }
//            }
//        }
//    }
//
//    Box(Modifier.fillMaxSize().then(selectable)) {
//        Column {
//            Surface(Modifier.fillMaxWidth(), tonalElevation = UI.elevation.lv1) {
//                Row(modifier = Modifier.height(UI.tabHeight)) {
////                    if (topLeft == structure && UI.isSmall) {
////                        AppDrawerIconButton()
////                    }
////                    if (structure in topRow)
////                        if (topRight != structure)
////                            Tabs(structure, onLayoutUpdate, layoutViewModel)
////                        else FixedEndLayout(end = { AppTopBarActions() }) {
////                            Row {
////                                Tabs(structure, onLayoutUpdate, layoutViewModel)
////                            }
////                        }
//                    Tabs(structure, onLayoutUpdate)
//                }
//            }
//
//            TintedHorizontalDivider()
//
//            Surface {
//                structure.tabs.getOrNull(structure.selected)?.let {
//                    Layout(it, onLayoutUpdate = { new ->
//                        onLayoutUpdate(structure.withTab(new, atIndex = structure.selected, replace = true))
//                    })
//                } ?: EmptyLayout(onLayoutUpdate = { layoutViewModel.openTab(WeekView()) })
////                if (structure.tabs.getOrNull(structure.selected)?.hasDropTargets != false)
//            }
//        }
//    }
//}
//
//
//@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
//@Composable
//private fun Tabs(
//    structure: LayoutStructure.Tabbed,
//    onLayoutUpdate: (LayoutStructure) -> Unit = {},
//) = BoxWithConstraints {
//    val layoutViewModel: LayoutViewModel by rememberGlobalViewModel()
//    val ui = LocalUIState.current
//    val minTabWidth = 150.dp
//    val maxTabWidth = 200.dp
//
//    fun onTabbedUpdate(structure: LayoutStructure) {
//        onLayoutUpdate(structure)
//        layoutViewModel.setActiveLayout(structure)
//    }
////    if (isActive) LaunchedEffect(structure) {
////        layoutViewModel.openFilesFlow.collectLatest { (content) ->
////            onTabbedUpdate(structure.withTab(content))
////        }
////    }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .horizontalScroll(rememberScrollState())
//            .height(UI.tabHeight)
//    ) {
//        fun closeTab(index: Int) {
//            if (structure.tabs.size == 1) {
//                onTabbedUpdate(LayoutStructure.Remove)
//            } else onTabbedUpdate(
//                structure.copy(
//                    tabs = structure.tabs.toMutableList().apply { removeAt(index) },
//                    selected = (structure.selected - 1).coerceAtLeast(0)
//                )
//            )
//        }
//        structure.name?.let {
//            Box(Modifier.padding(UI.padding.sm)) {
//                Text(it, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
//            }
//        }
//        Spacer(Modifier.width(UI.padding.sm))
//        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
//            if (structure.fullWidth) {
//                Box(
//                    Modifier.padding(ui.tabPadding),
//                    contentAlignment = Alignment.CenterStart
//                ) {
//                    (structure.tabs.firstOrNull() as? LayoutStructure.Single)?.tabLabel(Location.TabList)
//                }
//            } else structure.tabs.forEachIndexed { index, tab ->
//                Box(
//                    Modifier.widthIn(
//                        max = (this@BoxWithConstraints.maxWidth / structure.tabs.size)
//                            .coerceIn(minTabWidth, maxTabWidth)
//                    )
//                ) {
//                    LayoutTab(
//                        tab,
//                        selected = index == structure.selected,
//                        onDropLayout = { new -> onTabbedUpdate(structure.withTab(new, atIndex = index)) },
//                        onSelect = {
//                            onTabbedUpdate(structure.copy(selected = index))
//                        },
//                        onClose = { closeTab(index) }
//                    )
//                }
//            }
//        }
//        if (structure.selectable) BoxButton(onClick = {
//            layoutViewModel.openTab(LayoutStructure.Empty)
//        }) {
//            Icon(TablerIcons.Outlined.Plus, "Add tab", tint = MaterialTheme.colorScheme.onSurfaceVariant)
//        }
//        PlatformTopBarContainer(Modifier.fillMaxSize().weight(1f), {
//            HoverBox(
//                Modifier.fillMaxSize(),
//                onDropped = { new -> onTabbedUpdate(structure.withTab(new)) }
//            )
//        })
//    }
//
//    if (maxWidth < minTabWidth * structure.tabs.size) Box(
//        Modifier.width(UI.size.lg).fillMaxHeight().background(
//            brush = Brush.horizontalGradient(
//                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv1))
//            )
//        ).align(Alignment.CenterEnd)
//    )
//}
//
