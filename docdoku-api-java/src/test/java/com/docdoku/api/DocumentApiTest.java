/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.api;

import com.docdoku.api.client.ApiException;
import com.docdoku.api.models.DocumentCreationDTO;
import com.docdoku.api.models.DocumentIterationDTO;
import com.docdoku.api.models.DocumentRevisionDTO;
import com.docdoku.api.models.utils.LastIterationHelper;
import com.docdoku.api.services.DocumentApi;
import com.docdoku.api.services.FoldersApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

@RunWith(JUnit4.class)
public class DocumentApiTest {

    private DocumentApi documentApi = new DocumentApi(TestConfig.BASIC_CLIENT);
    private FoldersApi foldersApi = new FoldersApi(TestConfig.BASIC_CLIENT);

    @Test
    public void documentApiUsageTests() throws ApiException {

        // Create a document
        DocumentCreationDTO document = new DocumentCreationDTO();
        document.setReference(UUID.randomUUID().toString().substring(0,6));
        document.setTitle("GeneratedDoc");

        DocumentRevisionDTO createdDocument = foldersApi.createDocumentMasterInFolder(TestConfig.WORKSPACE, document, TestConfig.WORKSPACE);
        Assert.assertEquals(createdDocument.getDocumentMasterId(),document.getReference());

        // Check in
        DocumentRevisionDTO checkedInDocument = documentApi.checkInDocument(TestConfig.WORKSPACE, createdDocument.getDocumentMasterId(), createdDocument.getVersion(), "");
        Assert.assertEquals(checkedInDocument.getDocumentMasterId(),document.getReference());
        Assert.assertEquals(LastIterationHelper.getLastIteration(checkedInDocument).getIteration(), Integer.valueOf("1"));

        // Check out
        DocumentRevisionDTO checkedOutDocument = documentApi.checkOutDocument(TestConfig.WORKSPACE, createdDocument.getDocumentMasterId(), createdDocument.getVersion(), "");
        Assert.assertEquals(checkedOutDocument.getDocumentMasterId(),document.getReference());
        Assert.assertEquals(LastIterationHelper.getLastIteration(checkedOutDocument).getIteration(),Integer.valueOf("2"));

        // Undo check out
        DocumentRevisionDTO undoCheckOutDocument= documentApi.undoCheckOutDocument(TestConfig.WORKSPACE, createdDocument.getDocumentMasterId(), createdDocument.getVersion(), "");
        Assert.assertEquals(undoCheckOutDocument,checkedInDocument);

        // Check out
        checkedOutDocument = documentApi.checkOutDocument(TestConfig.WORKSPACE, createdDocument.getDocumentMasterId(), createdDocument.getVersion(), "");

        // Edit
        DocumentIterationDTO lastIteration = LastIterationHelper.getLastIteration(checkedOutDocument);
        Assert.assertNull(lastIteration.getModificationDate());
        lastIteration.setRevisionNote("Something modified");

        DocumentIterationDTO updatedIteration = documentApi.updateDocumentIteration(TestConfig.WORKSPACE, checkedOutDocument.getDocumentMasterId(), checkedOutDocument.getVersion(), "2", lastIteration);

        Assert.assertNotNull(updatedIteration.getModificationDate());
        lastIteration.setModificationDate(updatedIteration.getModificationDate());
        Assert.assertEquals(lastIteration,updatedIteration);

        // Check in
        checkedInDocument = documentApi.checkInDocument(TestConfig.WORKSPACE, createdDocument.getDocumentMasterId(), createdDocument.getVersion(), "");
        Assert.assertEquals(checkedInDocument.getDocumentIterations().size(),2);

    }



}