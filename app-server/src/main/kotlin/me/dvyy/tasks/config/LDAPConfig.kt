package me.dvyy.tasks.config

import io.ktor.server.application.*

class LDAPConfig(environment: ApplicationEnvironment) {
    val connection = environment.config.property("ldap.serverURL").getString()
    val userDNFormat = environment.config.property("ldap.userDNFormat").getString()
}
