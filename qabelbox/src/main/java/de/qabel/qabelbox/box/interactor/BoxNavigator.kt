package de.qabel.qabelbox.box.interactor

import de.qabel.box.storage.BoxNavigation
import de.qabel.box.storage.BoxVolume
import de.qabel.box.storage.exceptions.QblStorageNotFound
import de.qabel.qabelbox.box.dto.BoxPath
import javax.inject.Inject

class BoxNavigator @Inject constructor(
        keyAndPrefix: BoxFileBrowser.KeyAndPrefix,
        private val volume: BoxVolume): Navigator {

    override val key = keyAndPrefix.publicKey
    override val prefix = keyAndPrefix.prefix


    override val root: BoxNavigation by lazy {
        try {
            volume.navigate()
        } catch (e: QblStorageNotFound) {
            volume.createIndex("qabel", prefix)
            volume.navigate()
        }
    }


    override fun navigateTo(path: BoxPath, action: (BoxPath, BoxNavigation) -> Unit): BoxNavigation =
        if (path is BoxPath.Root || path.name == "") {
            root
        } else {
            val parent = navigateTo(path.parent, action)
            action(path, parent)
            parent.navigate(path.name)
        }
}
