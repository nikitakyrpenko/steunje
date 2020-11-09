package com.negeso.module.webshop.repository;

import com.negeso.module.webshop.entity.ECartItem;

import java.util.Collection;
import java.util.List;

public interface ECartItemRepository {

    Integer save(ECartItem cartItem);

    List<ECartItem> findAllByCartOwnerId(String cartOwnerId);

    void deleteAllByIds(Collection<Integer> ids);
}
