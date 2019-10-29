package seedu.mark.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import seedu.mark.commons.core.LogsCenter;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.bookmark.Folder;
import seedu.mark.model.bookmark.Url;
import seedu.mark.model.folderstructure.FolderStructure;

/**
 * Panel containing the tree of folders.
 */
public class FolderStructureTreeView extends UiPart<Region> {

    private static final String FXML = "FolderStructure.fxml";
    private final Logger logger = LogsCenter.getLogger(getClass());

    @FXML
    private TreeView<String> treeView;
    private HashMap<Folder, TreeItem<String>> mapOfFolderToTreeItem = new HashMap<>();
    private HashMap<TreeItem<String>, Url> mapOfTreeItemToUrl = new HashMap<>();
    private TreeItem<String> root;
    private ObservableList<Bookmark> bookmarks;
    private List<TreeItem<String>> bookmarkTreeItems = new ArrayList<>();

    /**
     * Instantiates a new Folder structure tree view.
     *
     * @param folderStructure the folder structure
     * @param bookmarks       the bookmarks
     */
    public FolderStructureTreeView(FolderStructure folderStructure,
                                   ObservableList<Bookmark> bookmarks,
                                   Consumer<Url> currentUrlChangeHandler) {
        super(FXML);
        this.bookmarks = bookmarks;
        root = buildTree(folderStructure);
        populateTreeWithBookmarks();
        bookmarks.addListener((ListChangeListener<? super Bookmark>) change -> {
            while (change.next()) {
                populateTreeWithBookmarks();
            }
        });
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Do nothing when selection is cleared
            if (newValue == null) {
                return;
            }
            if (!mapOfTreeItemToUrl.containsKey(newValue)) {
                return;
            }
            logger.info("Selection in folder structure tree view changed to: " + newValue);
            currentUrlChangeHandler.accept(mapOfTreeItemToUrl.get(newValue));
        });
    }


    /**
     * Builds the tree.
     * @param toBuild
     * @return
     */
    private TreeItem<String> buildTree(FolderStructure toBuild) {
        TreeItem<String> built = new TreeItem<>(toBuild.getFolder().folderName);
        ObservableList<FolderStructure> subfolders = toBuild.getSubfolders();
        for (FolderStructure subfolder: subfolders) {
            TreeItem<String> builtChild = buildTree(subfolder);
            mapOfFolderToTreeItem.put(subfolder.getFolder(), builtChild);
            built.getChildren().add(builtChild);
        }
        subfolders.addListener((ListChangeListener<? super FolderStructure>) change -> {
            while (change.next()) {
                for (FolderStructure oldFolderStructure : change.getRemoved()) {
                    TreeItem<String> oldFolderTreeItem = mapOfFolderToTreeItem.get(oldFolderStructure.getFolder());
                    oldFolderTreeItem.getParent().getChildren().remove(oldFolderTreeItem);
                    mapOfFolderToTreeItem.remove(oldFolderStructure.getFolder());
                    populateTreeWithBookmarks();
                }
                for (FolderStructure newFolderStructure : change.getAddedSubList()) {
                    TreeItem<String> newBuilt = buildTree(newFolderStructure);
                    mapOfFolderToTreeItem.put(newFolderStructure.getFolder(), newBuilt);
                    built.getChildren().add(newBuilt);
                    populateTreeWithBookmarks();
                }
            }
        });
        return built;
    }

    /**
     * Populates the folder tree with bookmarks.
     */
    private void populateTreeWithBookmarks() {
        for (TreeItem<String> oldBookmarkTreeItem: bookmarkTreeItems) {
            oldBookmarkTreeItem.getParent().getChildren().remove(oldBookmarkTreeItem);
        }
        bookmarkTreeItems = new ArrayList<>();
        mapOfTreeItemToUrl = new HashMap<>();
        for (Bookmark bookmark: bookmarks) {
            // if the folder is not found, we default it to the root
            TreeItem<String> treeItem = new TreeItem<>("Bookmark: " + bookmark);
            mapOfFolderToTreeItem.getOrDefault(bookmark.getFolder(), root)
                    .getChildren().add(treeItem);
            bookmarkTreeItems.add(treeItem);
            mapOfTreeItemToUrl.put(treeItem, bookmark.getUrl());
        }
    }

}
