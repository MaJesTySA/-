package com.inside.service;

import com.inside.pojo.Bgm;

import java.util.List;

public interface BgmService {
    List<Bgm> queryBgmList();

    Bgm queryById(String bgmId);

}
