package org.sandcast.k8s.informer;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class PodInformer {
    private static final Logger logger = Logger.getLogger(PodInformer.class.getSimpleName());

    public static void main(String[] args) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            SharedInformerFactory sharedInformerFactory = client.informers();
            SharedIndexInformer<Pod> podInformer = sharedInformerFactory.sharedIndexInformerFor(Pod.class, PodList.class, 5 * 60 * 1000L);
            logger.info("Informer factory initialized.");
            final CountDownLatch informerCompleted = new CountDownLatch(1);
            podInformer.addEventHandler(
                    new ResourceEventHandler<Pod>() {
                        @Override
                        public void onAdd(Pod pod) {
                            handlePodObject("ADDED", pod);
                        }

                        @Override
                        public void onUpdate(Pod oldPod, Pod newPod) {
                            handlePodObject("UPDATED", newPod);
                        }

                        @Override
                        public void onDelete(Pod pod, boolean deletedFinalStateUnknown) {
                            handlePodObject("DELETED", pod);
                        }
                    }
            );
            logger.info("Starting all registered informers");
            sharedInformerFactory.addSharedInformerEventListener(
                    exception -> logger.info("Exception occurred, but caught: " + exception.getMessage())
            );
            sharedInformerFactory.startAllRegisteredInformers();
            informerCompleted.await(); //hang out forever
        } catch (KubernetesClientException e) {
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void handlePodObject(String event, Pod pod) {
        StringBuilder builder = new StringBuilder()
                .append(event)
                .append(" pod ").append(pod.getMetadata().getName())
                .append(" created @").append(pod.getMetadata().getCreationTimestamp())
                .append(" state ").append(pod.getStatus().getPhase());
        logger.info(builder.toString());
    }
}