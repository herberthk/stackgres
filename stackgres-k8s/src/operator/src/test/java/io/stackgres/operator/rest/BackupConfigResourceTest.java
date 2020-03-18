/*
 * Copyright (C) 2019 OnGres, Inc.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package io.stackgres.operator.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.stackgres.operator.customresource.sgbackupconfig.StackGresBackupConfig;
import io.stackgres.operator.customresource.sgbackupconfig.StackGresBackupConfigList;
import io.stackgres.operator.resource.CustomResourceFinder;
import io.stackgres.operator.resource.CustomResourceScanner;
import io.stackgres.operator.resource.CustomResourceScheduler;
import io.stackgres.operator.rest.dto.backupconfig.BackupConfigDto;
import io.stackgres.operator.rest.transformer.AbstractResourceTransformer;
import io.stackgres.operator.rest.transformer.BackupConfigTransformer;
import io.stackgres.operator.utils.JsonUtil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BackupConfigResourceTest
    extends AbstractCustomResourceTest<BackupConfigDto, StackGresBackupConfig> {

  @Override
  protected CustomResourceList<StackGresBackupConfig> getCustomResourceList() {
    return JsonUtil
        .readFromJson("backup_config/list.json", StackGresBackupConfigList.class);
  }

  @Override
  protected BackupConfigDto getResourceDto() {
    return JsonUtil
        .readFromJson("backup_config/dto.json", BackupConfigDto.class);
  }

  @Override
  protected AbstractResourceTransformer<BackupConfigDto, StackGresBackupConfig> getTransformer() {
    return new BackupConfigTransformer();
  }

  @Override
  protected AbstractRestService<BackupConfigDto, StackGresBackupConfig> getService(
      CustomResourceScanner<StackGresBackupConfig> scanner,
      CustomResourceFinder<StackGresBackupConfig> finder,
      CustomResourceScheduler<StackGresBackupConfig> scheduler,
      AbstractResourceTransformer<BackupConfigDto, StackGresBackupConfig> transformer) {
    return new BackupConfigResource(scanner, finder, scheduler, transformer);
  }

  @Override
  protected String getResourceNamespace() {
    return "stackgres";
  }

  @Override
  protected String getResourceName() {
    return "backupconf";
  }

  @Override
  protected void checkBackupConfig(BackupConfigDto resource) {
    assertNotNull(resource.getMetadata());
    assertEquals("stackgres", resource.getMetadata().getNamespace());
    assertEquals("backupconf", resource.getMetadata().getName());
    assertEquals("93bc7621-0236-11ea-a1d5-0242ac110003", resource.getMetadata().getUid());
    assertNotNull(resource.getSpec());
    assertEquals("lz4", resource.getSpec().getCompressionMethod());
    assertEquals(0, resource.getSpec().getDiskRateLimit());
    assertEquals("*/1 * * * *", resource.getSpec().getFullSchedule());
    assertEquals(1, resource.getSpec().getFullWindow());
    assertEquals(0, resource.getSpec().getNetworkRateLimit());
    assertEquals(5, resource.getSpec().getRetention());
    assertNotNull(resource.getSpec().getStorage());
    assertNull(resource.getSpec().getStorage().getAzureblob());
    assertNull(resource.getSpec().getStorage().getGcs());
    assertEquals("s3compatible", resource.getSpec().getStorage().getType());
    assertNull(resource.getSpec().getStorage().getAzureblob());
    assertNull(resource.getSpec().getStorage().getGcs());
    assertNull(resource.getSpec().getStorage().getS3());
    assertNotNull(resource.getSpec().getStorage().getS3compatible());
    assertNotNull(resource.getSpec().getStorage().getS3compatible().getCredentials());
    assertEquals("http://minio.stackgres.svc:9000",
        resource.getSpec().getStorage().getS3compatible().getEndpoint());
    assertEquals("s3://stackgres", resource.getSpec().getStorage().getS3compatible().getPrefix());
    assertEquals("k8s", resource.getSpec().getStorage().getS3compatible().getRegion());
    assertNull(resource.getSpec().getStorage().getS3compatible().getStorageClass());
    assertTrue(resource.getSpec().getStorage().getS3compatible().isForcePathStyle());
  }

  @Override
  protected void checkBackupConfig(StackGresBackupConfig resource) {
    assertNotNull(resource.getMetadata());
    assertEquals("stackgres", resource.getMetadata().getNamespace());
    assertEquals("backupconf", resource.getMetadata().getName());
    assertEquals("93bc7621-0236-11ea-a1d5-0242ac110003", resource.getMetadata().getUid());
    assertNotNull(resource.getSpec());
    assertEquals("lz4", resource.getSpec().getCompressionMethod());
    assertEquals(0, resource.getSpec().getDiskRateLimit());
    assertEquals("*/1 * * * *", resource.getSpec().getFullSchedule());
    assertEquals(1, resource.getSpec().getFullWindow());
    assertEquals(0, resource.getSpec().getNetworkRateLimit());
    assertEquals(5, resource.getSpec().getRetention());
    assertNotNull(resource.getSpec().getStorage());
    assertNull(resource.getSpec().getStorage().getAzureblob());
    assertNull(resource.getSpec().getStorage().getGcs());
    assertEquals("s3", resource.getSpec().getStorage().getType());
    assertNull(resource.getSpec().getStorage().getAzureblob());
    assertNull(resource.getSpec().getStorage().getGcs());
    assertNotNull(resource.getSpec().getStorage().getS3());
    assertNotNull(resource.getSpec().getStorage().getS3().getCredentials());
    assertEquals("http://minio.stackgres.svc:9000", resource.getSpec().getStorage().getS3().getEndpoint());
    assertEquals("s3://stackgres", resource.getSpec().getStorage().getS3().getPrefix());
    assertEquals("k8s", resource.getSpec().getStorage().getS3().getRegion());
    assertNull(resource.getSpec().getStorage().getS3().getStorageClass());
    assertTrue(resource.getSpec().getStorage().getS3().isForcePathStyle());
  }

}