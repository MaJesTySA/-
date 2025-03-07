package person.sa.service;

import person.sa.pojo.Users;
import person.sa.utils.PagedResult;

public interface UsersService {

    PagedResult queryUsers(Users user, Integer page, Integer pageSize);

}
