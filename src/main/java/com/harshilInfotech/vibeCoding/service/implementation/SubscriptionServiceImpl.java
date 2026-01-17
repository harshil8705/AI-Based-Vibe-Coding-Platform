package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.dto.subscription.SubscriptionResponse;
import com.harshilInfotech.vibeCoding.entity.Plan;
import com.harshilInfotech.vibeCoding.entity.Subscription;
import com.harshilInfotech.vibeCoding.entity.User;
import com.harshilInfotech.vibeCoding.enums.SubscriptionStatus;
import com.harshilInfotech.vibeCoding.error.ResourceNotFoundException;
import com.harshilInfotech.vibeCoding.mapper.SubscriptionMapper;
import com.harshilInfotech.vibeCoding.repository.PlanRepository;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import com.harshilInfotech.vibeCoding.repository.SubscriptionRepository;
import com.harshilInfotech.vibeCoding.repository.UserRepository;
import com.harshilInfotech.vibeCoding.security.AuthUtil;
import com.harshilInfotech.vibeCoding.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 1;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();

        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE,
                SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING
        )).orElse(
                new Subscription()
        );

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {

        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);

        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();
        subscriptionRepository.save(subscription);

    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean subscriptionHasBeenUpdated = false;

        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            subscriptionHasBeenUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(periodStart);
            subscriptionHasBeenUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            subscriptionHasBeenUpdated = true;
        }

        if (cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            subscriptionHasBeenUpdated = true;

            if (cancelAtPeriodEnd) {
                subscription.setStatus(SubscriptionStatus.CANCELED);
                log.info("Subscription {} marked as Canceled (will end at period end: {})",
                        gatewaySubscriptionId, periodEnd);
            }
        }

        if (planId != null && planId != subscription.getPlan().getId()) {

            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            subscriptionHasBeenUpdated = true;

        }

        if (subscriptionHasBeenUpdated) {
            log.debug("Subscription has been updated: {}", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {

        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() != SubscriptionStatus.CANCELED) {
            subscription.setStatus(SubscriptionStatus.CANCELED);
            subscriptionRepository.save(subscription);
            log.info("Subscription {} has been canceled", gatewaySubscriptionId);
        } else {
            log.info("Subscription {} was already marked as CANCELED, now actually deleted from Stripe", gatewaySubscriptionId);
        }

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription is already past due, gatewaySubscriptionId: {}", gatewaySubscriptionId);
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

    }

    @Override
    public boolean canCreateProject() {

        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);

        if (currentSubscription.plan() == null) {
            return countOfOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
        }

        return countOfOwnedProjects < currentSubscription.plan().maxProjects();

    }

    //    Utility Methods:->>
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plain", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId).orElseThrow(() ->
                new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
