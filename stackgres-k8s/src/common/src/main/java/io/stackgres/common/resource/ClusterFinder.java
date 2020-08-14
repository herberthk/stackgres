/*
 * Copyright (C) 2019 OnGres, Inc.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package io.stackgres.common.resource;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.stackgres.common.KubernetesClientFactory;
import io.stackgres.common.crd.sgcluster.StackGresCluster;
import io.stackgres.common.crd.sgcluster.StackGresClusterDefinition;
import io.stackgres.common.crd.sgcluster.StackGresClusterDoneable;
import io.stackgres.common.crd.sgcluster.StackGresClusterList;

@ApplicationScoped
public class ClusterFinder implements CustomResourceFinder<StackGresCluster> {

  private KubernetesClientFactory kubernetesClientFactory;

  @Inject
  public ClusterFinder(KubernetesClientFactory kubernetesClientFactory) {
    this.kubernetesClientFactory = kubernetesClientFactory;
  }

  @Override
  public Optional<StackGresCluster> findByNameAndNamespace(String name, String namespace) {

    try (KubernetesClient client = kubernetesClientFactory.create()) {

      return ResourceUtil.getCustomResource(client, StackGresClusterDefinition.NAME)
          .map(CustomResourceDefinitionContext::fromCrd)
          .map(crd -> client.customResources(crd,
              StackGresCluster.class,
              StackGresClusterList.class,
              StackGresClusterDoneable.class)
              .inNamespace(namespace)
              .withName(name)
              .get());

    }
  }
}
