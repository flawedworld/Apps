package org.grapheneos.apps.client.ui.mainScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.grapheneos.apps.client.App
import org.grapheneos.apps.client.R
import org.grapheneos.apps.client.databinding.ItemAppsBinding
import org.grapheneos.apps.client.uiItem.InstallStatus
import org.grapheneos.apps.client.uiItem.InstallablePackageInfo

class AppsListAdapter(private val onItemClick: (packageName: String) -> Unit) :
    ListAdapter<InstallablePackageInfo, AppsListAdapter.AppsListViewHolder>(
        InstallablePackageInfo.UiItemDiff()
    ) {

    inner class AppsListViewHolder(
        private val binding: ItemAppsBinding,
        private val onItemClick: (packageName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val currentItem = currentList[position]
            val status = currentItem.installStatus

            binding.appName.text = currentItem.name
            binding.install.text = currentItem.installStatus.status
            binding.latestVersion.text = status.latestV
            binding.installedVersion.text = status.installedV

            binding.install.setOnClickListener {
                onItemClick.invoke(currentItem.name)
            }

            binding.apply {

                var isIndeterminateProcessing = false
                var isDownloading = false
                var enableActionButton = true

                when (status) {
                    is InstallStatus.Installed -> {
                        install.text = App.getString(R.string.uninstall)
                    }
                    is InstallStatus.Installing -> {
                        enableActionButton= status.canCancelTask
                        install.text = App.getString(R.string.installing)
                        isIndeterminateProcessing = true
                    }
                    is InstallStatus.Uninstalling -> {
                        enableActionButton = false
                        install.text = App.getString(R.string.uninstalling)
                        isIndeterminateProcessing = true
                    }
                    is InstallStatus.Downloading -> {
                        val sizeInfo = "${status.downloadedSize.toMB()} MB out of " +
                                "${status.downloadSize.toMB()} MB," +
                                "  ${status.downloadedPercent} %"
                        isIndeterminateProcessing = (status.downloadSize == 0) && (status.downloadedSize == 0)
                        isDownloading = !isIndeterminateProcessing
                        install.text = status.status
                        downloadProgress.setProgressCompat(status.downloadedPercent, true)
                        downloadSizeInfo.text = sizeInfo
                    }
                    is InstallStatus.Installable -> {
                        enableActionButton = !status.isClicked
                        install.text = App.getString(R.string.install)
                    }
                    is InstallStatus.Updated -> {
                        install.text = App.getString(R.string.updated)
                    }
                    is InstallStatus.Failed -> {
                        install.text = App.getString(R.string.failedRetry)
                    }
                    is InstallStatus.Updatable -> {
                        install.text = App.getString(R.string.update)
                    }
                    is InstallStatus.ReinstallRequired -> {
                        install.text = App.getString(R.string.reinstallRequired)
                    }
                }

                downloadProgress.isInvisible = !(isIndeterminateProcessing || isDownloading)
                downloadProgress.isIndeterminate = isIndeterminateProcessing
                downloadSizeInfo.isGone = !isDownloading
                install.isEnabled = enableActionButton
            }
        }

    }

    private fun Int.toMB() : String = String.format("%.3f", (this / 1024.0 / 1024.0))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AppsListViewHolder(
        ItemAppsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onItemClick
    )

    override fun onBindViewHolder(holder: AppsListViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = currentList.size

}
