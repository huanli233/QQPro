package momoi.plugin.apkmixin

import groovy.lang.Closure
import momoi.plugin.apkmixin.utils.child
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.util.internal.ConfigureUtil
import java.io.File
import kotlin.math.sign

const val DEFAULT_MIXIN_APK_NAME = "mixin.apk"
const val DEFAULT_UNSIGNED_APK_NAME = "unsigned.apk"
const val DEFAULT_SIGNED_APK_NAME = "signed.apk"

open class ApkMixinExtension {
    var versionName = ""
    var targetApk: String? = null
    var output = OutputExtension()
    var signing = SigningExtension()
    var useProcessorCountAsThreadCount = false

    fun output(action: Action<OutputExtension>) {
        action.execute(output)
    }

    fun signing(action: Action<SigningExtension>){
        action.execute(signing)
    }
}

open class SigningExtension {
    var enabled = true
    var keyFile: File? = null
    var certFile: File? = null
}

open class OutputExtension {
    var outputDir: String = "dist"
    var unsignedFileName: String = DEFAULT_UNSIGNED_APK_NAME
    var signedFileName: String = DEFAULT_SIGNED_APK_NAME
    var mixinApkFileName: String = DEFAULT_MIXIN_APK_NAME
}

internal fun Project.outputDir(extension: ApkMixinExtension) = projectDir.child(extension.output.outputDir).apply {
    if (!exists()) mkdirs()
}