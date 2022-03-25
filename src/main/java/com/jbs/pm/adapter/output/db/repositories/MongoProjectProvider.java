package com.jbs.pm.adapter.output.db.repositories;

import com.jbs.pm.adapter.output.db.documents.ProjectDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoProjectProvider extends MongoRepository<ProjectDocument, String> {

  List<ProjectDocument> findByLeaderId(String leaderId);

  List<ProjectDocument> findByFullNameLikeIgnoreCase(String fullName);

  List<ProjectDocument> findByLeaderIdLikeIgnoreCase(String leaderId);
}
