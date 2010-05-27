package org.apache.archiva.web.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.archiva.web.test.parent.AbstractBrowseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test( groups = { "browse" }, dependsOnMethods = { "testAddArtifactNullValues" } )
public class BrowseTest
    extends AbstractBrowseTest
{
    public void testBrowseArtifactPageTabs()
    {
        goToBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_GROUPID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_VERSION" ) + "/" );
        
        assertTextPresent( "Info" );
        assertTextPresent( "Dependencies" );
        assertTextPresent( "Dependency Tree" );
        assertTextPresent( "Used By" );
        assertTextPresent( "Mailing Lists" );
        assertTextPresent( "Metadata" );
    }

    public void testBrowseArtifact()
    {
        goToBrowsePage();
        assertBrowsePage();
    }

    public void testClickArtifactFromBrowse()
    {
        goToBrowsePage();
        assertBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        assertPage( "Apache Archiva \\ Browse Repository" );
        assertTextPresent( "Artifacts" );
    }

    // MRM-1278
    @Test( groups = { "requiresUpload" } )
    public void testCorrectRepositoryInBrowse()
    {
        String releasesRepo = getProperty( "RELEASES_REPOSITORY" );

        // create releases repository first
        goToRepositoriesPage();
        clickLinkWithText( "Add" );
        addManagedRepository( getProperty( "RELEASES_REPOSITORY" ), "Releases Repository",
                              new File( getBasedir(), "target/repository/releases" ).getPath(), "",
                              "Maven 2.x Repository", "0 0 * * * ?", "", "" );
        assertTextPresent( "Releases Repository" );

        String snapshotsRepo = getProperty( "SNAPSHOTS_REPOSITORY" );

        String path =
            "src/test/resources/snapshots/org/apache/maven/archiva/web/test/foo-bar/1.0-SNAPSHOT/foo-bar-1.0-SNAPSHOT.jar";
        // TODO: do this differently as uploading doesn't work on browsers other than *chrome (below as well)
        // upload a snapshot artifact to repository 'releases'
        addArtifact( "archiva", "archiva-webapp", "1.0-SNAPSHOT", "jar", path, releasesRepo );
        assertTextPresent( "Artifact 'archiva:archiva-webapp:1.0-SNAPSHOT' was successfully deployed to repository '"
            + releasesRepo + "'" );

        goToBrowsePage();
        assertBrowsePage();
        assertGroupsPage( "archiva/" );
        assertArtifactsPage( "archiva-webapp/" );
        assertArtifactInfoPage( "1.0-SNAPSHOT/", releasesRepo, "archiva", "archiva-webapp", "1.0-SNAPSHOT", "jar" );

        // upload a snapshot artifact to repository 'snapshots'
        addArtifact( "continuum", "continuum-core", "1.0-SNAPSHOT", "jar", path, snapshotsRepo );
        assertTextPresent( "Artifact 'continuum:continuum-core:1.0-SNAPSHOT' was successfully deployed to repository '"
            + snapshotsRepo + "'" );

        goToBrowsePage();
        assertBrowsePage();
        assertGroupsPage( "continuum/" );
        assertArtifactsPage( "continuum-core/" );
        assertArtifactInfoPage( "1.0-SNAPSHOT/", snapshotsRepo, "continuum", "continuum-core", "1.0-SNAPSHOT", "jar" );
    }

    // MRM-1353
    @Test( groups = { "requiresUpload" } )
    public void testBuildNumberOfSnapshotArtifact()
    {
        String snapshotsRepo = getProperty( "SNAPSHOTS_REPOSITORY" );

        String path =
            "src/test/resources/snapshots/org/apache/maven/archiva/web/test/foo-bar/1.0-SNAPSHOT/foo-bar-1.0-SNAPSHOT.jar";
        // TODO: do this differently as uploading doesn't work on browsers other than *chrome (below as well)
        // upload a snapshot artifact to repository 'releases'
        addArtifact( "archiva", "archiva-multiple-artifacts", "1.0-SNAPSHOT", "jar", path, snapshotsRepo );
        assertTextPresent( "Artifact 'archiva:archiva-multiple-artifacts:1.0-SNAPSHOT' was successfully deployed to repository '"
            + snapshotsRepo + "'" );

        goToBrowsePage();
        assertBrowsePage();
        assertGroupsPage( "archiva/" );
        assertArtifactsPage( "archiva-multiple-artifacts/" );
        assertArtifactInfoPage( "1.0-SNAPSHOT/", snapshotsRepo, "archiva", "archiva-multiple-artifacts", "1.0-SNAPSHOT", "jar" );


        addArtifact( "archiva", "archiva-multiple-artifacts", "1.0-SNAPSHOT", "jar", path, snapshotsRepo );
        assertTextPresent( "Artifact 'archiva:archiva-multiple-artifacts:1.0-SNAPSHOT' was successfully deployed to repository '"
            + snapshotsRepo + "'" );

        goToBrowsePage();
        assertBrowsePage();
        assertGroupsPage( "archiva/" );
        assertArtifactsPage( "archiva-multiple-artifacts/" );
        assertArtifactInfoPage( "1.0-SNAPSHOT/", snapshotsRepo, "archiva", "archiva-multiple-artifacts", "1.0-SNAPSHOT", "jar" );

        String firstSnapshotVersion = getText( "//div[@id='download']/div[@id='accordion']/p[2]/a/" );
        Assert.assertTrue( firstSnapshotVersion.endsWith( "-1" ) );

        String secondSnapshotVersion = getText( "//div[@id='download']/div[@id='accordion']/p[1]/a/" );
        Assert.assertTrue( secondSnapshotVersion.endsWith( "-2" ) );
    }
    
    public void testMetadataPageDisplay()
    {
        goToBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_GROUPID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_VERSION" ) + "/" );
        clickLinkWithText( "Metadata" );
        
        assertMinimalMetadataDisplay();
    }
    
    public void testDeleteMetadataDependency()
    {
        String depArtifactId = "testArtifactId";
        
        goToBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_GROUPID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_VERSION" ) + "/" );
        clickLinkWithText( "Metadata" );
        
        assertMinimalMetadataDisplay();
        assertTextPresent( "dependencies" );
        assertTextPresent( depArtifactId );
        
        String xPath = "//li[contains(text(),'" + depArtifactId + "')]/a/img[contains(@src,'delete.gif')]";
        clickLinkWithXPath( xPath );
        
        assertTextNotPresent( depArtifactId );
    }
    
    public void testDeleteMetadataMailingList()
    {
        String listName = "test user list";
        
        goToBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_GROUPID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_VERSION" ) + "/" );
        clickLinkWithText( "Metadata" );
        
        assertMinimalMetadataDisplay();
        assertTextPresent( "mailingLists" );
        assertTextPresent( listName );
        
        String xPath = "//li[contains(text(),'" + listName + "')]/a/img[contains(@src,'delete.gif')]";
        clickLinkWithXPath( xPath );
        
        assertTextNotPresent( listName );
    }
    
    public void testDeleteMetadataLicense()
    {
        String licenseName = "Test License";
        
        goToBrowsePage();
        clickLinkWithText( getProperty( "ARTIFACT_GROUPID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_ARTIFACTID" ) + "/" );
        clickLinkWithText( getProperty( "ARTIFACT_VERSION" ) + "/" );
        clickLinkWithText( "Metadata" );
        
        assertMinimalMetadataDisplay();
        assertTextPresent( "licenses" );
        assertTextPresent( licenseName );
        
        String xPath = "//li[contains(text(),'" + licenseName + "')]/a/img[contains(@src,'delete.gif')]";
        clickLinkWithXPath( xPath );
        
        assertTextNotPresent( licenseName );
        assertTextNotPresent( "licenses" );
    }
    
    private void assertMinimalMetadataDisplay()
    {
        assertTextPresent( "project.metadata.id=" );
        assertTextPresent( "project.url=" );
        assertTextPresent( "project.name=" );
        assertTextPresent( "project.description=" );
    }

    private void assertArtifactInfoPage( String version, String artifactInfoRepositoryId, String artifactInfoGroupId,
                                         String artifactInfoArtifactId, String artifactInfoVersion,
                                         String artifactInfoPackaging )
    {
        clickLinkWithText( version );
        assertPage( "Apache Archiva \\ Browse Repository" );
        assertTextPresent( artifactInfoRepositoryId );
        assertTextPresent( artifactInfoGroupId );
        assertTextPresent( artifactInfoArtifactId );
        assertTextPresent( artifactInfoVersion );
        assertTextPresent( artifactInfoPackaging );
    }

    private void assertArtifactsPage( String artifactId )
    {
        clickLinkWithText( artifactId );
        assertPage( "Apache Archiva \\ Browse Repository" );
        assertTextPresent( "Versions" );
    }

    private void assertGroupsPage( String groupId )
    {
        clickLinkWithText( groupId );
        assertPage( "Apache Archiva \\ Browse Repository" );
        assertTextPresent( "Artifacts" );
    }
}
