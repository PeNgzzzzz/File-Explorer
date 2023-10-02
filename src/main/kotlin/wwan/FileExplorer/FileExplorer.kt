package wwan.FileExplorer

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.scene.text.TextFlow
import javafx.stage.FileChooser
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files

class FileExplorer : Application() {

    private val keyEnter = KeyCode.ENTER
    private val keyBSpace = KeyCode.BACK_SPACE
    private val keyDelete = KeyCode.DELETE

    private val startDir = File("${System.getProperty("user.dir")}/test/")
    private var curDir = startDir
    private var fileList = curDir.listFiles()
    private var fileNames = getFileNames()
    private var listView = ListView<String>(FXCollections.observableArrayList(fileNames))

    override fun start(stage: Stage) {
        // StatusLine
        val statusLine = BorderPane().apply {
            padding = Insets(5.0)
        }

        // MenuBar
        val menuBar = MenuBar()
        menuBar.menus.addAll(
            // File Menu
            Menu("File").apply {
                items.add(MenuItem("Quit").apply {
                    onAction = EventHandler { Platform.exit() }
                })
            },
            // View Menu
            Menu("View").apply {
            },
            // Actions Menu
            Menu("Actions").apply {
                items.add(MenuItem("Home").apply {
                    onAction = EventHandler {
                        updateCurDir(startDir, statusLine)
                    }
                })
                items.add(MenuItem("Prev").apply {
                    onAction = EventHandler {
                        if (curDir != startDir) {
                            updateCurDir(curDir.parentFile, statusLine)
                        }
                    }
                })
                items.add(MenuItem("Next"))
                items.add(MenuItem("Delete"))
                items.add(MenuItem("Rename"))
                items.add(MenuItem("Move"))
            },
            // Options Menu
            Menu("Options").apply {
            }
        )

        // Toolbar
        val toolBar = ToolBar()
        val b1 = Button("Home").apply {
            graphic = ImageView(Image("home.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
            onAction = EventHandler {
                updateCurDir(startDir, statusLine)
            }
        }
        val b2 = Button("Prev").apply {
            graphic = ImageView(Image("prev.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
            onAction = EventHandler {
                if (curDir != startDir) {
                    updateCurDir(curDir.parentFile, statusLine)
                }
            }
        }
        val b3 = Button("Next").apply {
            graphic = ImageView(Image("next.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
        }
        val b4 = Button("Delete").apply {
            graphic = ImageView(Image("delete.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
        }
        val b5 = Button("Rename").apply {
            graphic = ImageView(Image("rename.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
        }
        val b6 = Button("Move").apply {
            graphic = ImageView(Image("move.png")).apply {
                fitWidth = 20.0
                fitHeight = 20.0
            }
        }
        toolBar.items.addAll(b1, b2, b3, b4, b5, b6)

        // Files (most complicated part)
        val fileView = BorderPane().apply {
            padding = Insets(5.0)
            minWidth = 0.0
            minHeight = 0.0
        }

        listView.selectionModel.selectedItemProperty().addListener { _, _, curFileName ->
            fileView.children.clear()
            val curPathName = curDir.absolutePath + "/" + curFileName
            val curSelected = File(curPathName)
            statusLine.left = Label(curPathName)
            try {
                if (curSelected.isFile) {
                    // Select a file
                    if (!curSelected.canRead()) {
                        // The file is unreadable
                        fileView.center = Text("File cannot be read").apply {
                            fill = Color.RED
                        }
                    } else {
                        val type = fileType(curFileName)
                        if (type == 0) {
                            // Unsupported type
                            fileView.center = Text("Unsupported type").apply {
                                fill = Color.RED
                            }
                        } else if (type == 1) {
                            // Image
                            val image = ImageView(Image(FileInputStream(curPathName))).apply {
                                fitWidthProperty().bind(fileView.widthProperty())
                                fitHeightProperty().bind(fileView.heightProperty())
                            }
                            fileView.center = image
                        } else {
                            // Text
                            val text = Text(curSelected.bufferedReader().readText())
                            val textFlow = TextFlow(text).apply {
                                lineSpacing = 2.0
                                TextAlignment.LEFT
                            }
                            fileView.center = ScrollPane(textFlow).apply {
                                viewportBoundsProperty().addListener { _, _, new ->
                                    textFlow.setPrefSize(new.width, new.height)
                                }
                            }
                        }
                    }
                }
            } catch (e : IOException) {
                fileView.center = Text("Fail to select").apply {
                    fill = Color.RED
                }
            }
            // Next in menuBar
            menuBar.menus[2].items[2].setOnAction {
                if (curSelected.isDirectory) {
                    updateCurDir(curSelected, statusLine)
                }
            }
            // Delete in menuBar
            menuBar.menus[2].items[3].setOnAction {
                if (curFileName != null) delete(curSelected, statusLine)
            }
            // Rename in menuBar
            menuBar.menus[2].items[4].setOnAction {
                if (curFileName != null) rename(curSelected, statusLine)
            }
            // Move in menuBar
            menuBar.menus[2].items[5].setOnAction {
                if (curFileName != null) move(curSelected, statusLine, stage)
            }

            // Next button
            b3.setOnAction {
                if (curSelected.isDirectory) {
                    updateCurDir(curSelected, statusLine)
                }
            }
            // Delete button
            b4.setOnAction {
                if (curFileName != null) delete(curSelected, statusLine)
            }
            // Rename button
            b5.setOnAction {
                if (curFileName != null) rename(curSelected, statusLine)
            }
            // Move button
            b6.setOnAction {
                if (curFileName != null) move(curSelected, statusLine, stage)
            }

            // Hotkeys
            listView.setOnKeyPressed { e ->
                if (e.code.equals(keyEnter) && curSelected.isDirectory) updateCurDir(curSelected, statusLine)
                else if (e.code.equals(keyBSpace) || e.code.equals(keyDelete)) {
                    updateCurDir(curDir.parentFile, statusLine)
                }
            }

            // Double-click
            listView.setOnMouseClicked { e ->
                if (e.clickCount == 2 && curSelected.isDirectory) updateCurDir(curSelected, statusLine)
            }
        }

        // Combine menuBar and toolBar into one VBox
        val menuAndTool = VBox(menuBar, toolBar).apply {
            padding = Insets(5.0)
        }

        // Store the list of files in StackPane
        val dirView = StackPane(listView).apply {
            minWidth = 0.0
            minHeight = 0.0
        }

        // Some space will be put on the right of BorderPane
        val space = Pane().apply {
            minWidth = 5.0
            minHeight = 0.0
        }

        val root = BorderPane().apply {
            padding = Insets(5.0)
            top = menuAndTool
            left = dirView
            center = fileView
            right = space
            bottom = statusLine
        }

        stage.apply {
            scene = Scene(root, 850.0, 550.0)
            title = "File Explorer"
        }.show()
    }

    /* Reset status line to empty because nothing is selected */
    private fun resetStatus(statusView: BorderPane) {
        statusView.left = Label()
    }

    /* Update current directory and refresh the GUI */
    private fun updateCurDir(dir: File, statusView: BorderPane) {
        curDir = dir
        fileList = curDir.listFiles()
        fileNames = getFileNames()
        listView.items = FXCollections.observableArrayList(fileNames)
        resetStatus(statusView)
    }

    /* Return a list of file names as strings under current directory */
    private fun getFileNames(): MutableList<String> {
        fileNames = mutableListOf<String>()
        for (file in fileList) {
            if (file.isFile) fileNames.add(file.name)
            else fileNames.add(file.name + "/")
        }
        fileNames.sort()
        return fileNames
    }

    /* Return 1 if the file type is image, 2 if the file type is text, 0 otherwise */
    private fun fileType(fileName: String): Int {
        if (fileName.endsWith("png") || fileName.endsWith("jpg") || fileName.endsWith("bmp")) return 1
        if (fileName.endsWith("txt") || fileName.endsWith("md")) return 2
        return 0
    }

    /* Delete targetFile */
    private fun delete(targetFile: File, statusView: BorderPane) {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Delete Alert"
            if (targetFile.isFile) contentText = "Are you sure you want to delete the file ${targetFile.name} ?"
            else contentText = "Are you sure you want to delete the folder ${targetFile.name} ?"
        }
        val response = alert.showAndWait()
        if (response.isPresent) {
            try {
                if (response.get() == ButtonType.OK) {
                    if (targetFile.isFile) targetFile.delete()
                    else targetFile.deleteRecursively()
                    updateCurDir(curDir, statusView)
                }
            } catch (e : IOException) {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "Error!"
                    if (targetFile.isFile) contentText = "Fail to delete the file ${targetFile.name}"
                    else contentText = "Fail to delete the folder ${targetFile.name}"
                }.showAndWait()
            }
        }
    }

    /* Rename targetFile */
    private fun rename(targetFile: File, statusView: BorderPane) {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Rename Alert"
            if (targetFile.isFile) contentText = "Are you sure you want to rename the file ${targetFile.name} ?"
            else contentText = "Are you sure you want to rename the folder ${targetFile.name} ?"
        }
        val response = alert.showAndWait()
        if (response.isPresent) {
            try {
                if (response.get() == ButtonType.OK) {
                    val dialog = TextInputDialog("").apply {
                        title = "Rename Dialog"
                        headerText = "Please enter a new name"
                    }
                    val input = dialog.showAndWait()
                    if (input.isPresent) {
                        val newName = input.get()
                        val newFile = File(curDir.absolutePath + "/" + newName)
                        val result = targetFile.renameTo(newFile)
                        if (!result) throw(IOException())
                        updateCurDir(curDir, statusView)
                    }
                }
            } catch (e : IOException) {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "Error!"
                    if (targetFile.isFile) contentText = "Fail to rename the file ${targetFile.name}"
                    else contentText = "Fail to rename the folder ${targetFile.name}"
                }.showAndWait()
            }
        }
    }

    /* Move targetFile */
    private fun move(targetFile: File, statusView: BorderPane, stage: Stage) {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Move Alert"
            if (targetFile.isFile) contentText = "Are you sure you want to move the file ${targetFile.name} ?"
            else contentText = "Are you sure you want to move the folder ${targetFile.name} ?"
        }
        val response = alert.showAndWait()
        if (response.isPresent) {
            try {
                if (response.get() == ButtonType.OK) {
                    val destination = FileChooser().showSaveDialog(stage)
                    Files.move(targetFile.toPath(), destination.toPath())
                    updateCurDir(destination.parentFile, statusView)
                }
            } catch (e : IOException) {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "Error!"
                    if (targetFile.isFile) contentText = "Fail to move the file ${targetFile.name}"
                    else "Fail to move the folder ${targetFile.name}"
                }.showAndWait()
            }
        }
    }

}

fun main() {
    Application.launch(FileExplorer::class.java)
}