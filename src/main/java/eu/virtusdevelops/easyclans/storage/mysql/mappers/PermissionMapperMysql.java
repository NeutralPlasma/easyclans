package eu.virtusdevelops.easyclans.storage.mysql.mappers;

import eu.virtusdevelops.easyclans.models.UserPermissions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class PermissionMapperMysql implements Function<ResultSet, UserPermissions> {
    @Override
    public UserPermissions apply(ResultSet rs) {
        try{
            var permissionName = rs.getString("ec_permission.permission");
            return UserPermissions.valueOf(permissionName);
        }catch (SQLException e){
            return null;
        }
    }
}
