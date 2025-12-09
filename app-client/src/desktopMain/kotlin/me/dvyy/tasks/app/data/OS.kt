package me.dvyy.tasks.app.data

sealed class OS {
    data object WINDOWS : OS()
    data object LINUX : OS()
    data object MAC : OS()

    companion object {
        fun isArm(): Boolean {
            return System.getProperty("os.arch", "unknown").lowercase().contains("arm")
        }

        fun get(): OS {
            val os = System.getProperty("os.name").lowercase()
            return when {
                "win" in os -> WINDOWS
                "nix" in os || "nux" in os || "aix" in os -> LINUX
                "mac" in os -> MAC
                else -> error("Unsupported")
            }
        }
    }
}

sealed class Arch {
    data object X64 : Arch()
    data object X86 : Arch()
    data object ARM64 : Arch()
    data object ARM32 : Arch()
    data object Unknown : Arch()


    companion object {
        fun get(): Arch {
            val archString = System.getProperty("os.arch", "unknown")
            return when (archString) {
                "amd64", "x86_64" -> X64
                "x86" -> X86
                "aarch64" -> ARM64
                "arm" -> ARM32
                else -> Unknown
            }
        }
    }
}
