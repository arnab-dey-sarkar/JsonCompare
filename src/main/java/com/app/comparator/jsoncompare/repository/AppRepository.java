package com.app.comparator.jsoncompare.repository;

import com.app.comparator.jsoncompare.model.AppModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<AppModel,String> {
}
