package net.hyren.core.shared.users.passwords.storage.repositories.implementations

import net.hyren.core.shared.users.passwords.data.UserPassword
import net.hyren.core.shared.users.passwords.storage.dao.UserPasswordDAO
import net.hyren.core.shared.users.passwords.storage.dto.CreateUserPasswordDTO
import net.hyren.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import net.hyren.core.shared.users.passwords.storage.repositories.IUsersPasswordsRepository
import net.hyren.core.shared.users.passwords.storage.table.UserPasswordTable
import net.hyren.core.shared.users.storage.table.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Gutyerrez
 */
class MariaDBUsersPasswordsRepository : IUsersPasswordsRepository {

    override fun fetchByUserId(fetchUserPasswordByUserIdDTO: FetchUserPasswordByUserIdDTO): List<UserPassword> {
        return transaction {
            addLogger(StdOutSqlLogger)

            return@transaction UserPasswordDAO.find {
                UserPasswordTable.userId eq fetchUserPasswordByUserIdDTO.userId
            }.map { it.asUserPassword() }
        }
    }

    override fun create(createUserPasswordDTO: CreateUserPasswordDTO) {
        transaction {
            UserPasswordDAO.find {
                UserPasswordTable.userId eq createUserPasswordDTO.userId and (
                    UserPasswordTable.enabled eq true
                )
            }.forEach { it.enabled = false }

            UserPasswordDAO.new {
                this.userId = EntityID(
                    createUserPasswordDTO.userId,
                    UsersTable
                )
                this.password = createUserPasswordDTO.password
            }
        }
    }

}