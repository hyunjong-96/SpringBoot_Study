package com.example.querydsl.user.doamin;

import java.util.List;

public interface UserRepositoryCustom {
	List<User> getUserListByQueryDsl();
	List<User> getSameNameUserListByQueryDsl(String name);
}
