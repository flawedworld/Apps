package org.grapheneos.apps.client.item

data class Package(
    val packageName: String,
    val variants: List<PackageVariant>,
)