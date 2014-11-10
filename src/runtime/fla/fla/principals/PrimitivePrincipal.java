package fla.principals;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fla.util.ActsForQuery;
import fla.util.DelegationPair;

public class PrimitivePrincipal extends Principal {
  /**
   * Maps labels L to delegation pairs labelled with L.
   */
  private final Map<Principal, Set<DelegationPair>> delegations;

  public final String name;

  /**
   * @param name a name for this principal, for {@code toString()} purposes.
   */
  public PrimitivePrincipal(String name) {
    this.delegations = new HashMap<>();
    this.name = name;
  }

  @Override
  public boolean equals(Principal p) {
    return this == p;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public final ConfPrincipal confidentiality() {
    return new ConfPrincipal(this);
  }

  @Override
  public final IntegPrincipal integrity() {
    return new IntegPrincipal(this);
  }

  @Override
  public final Principal project(Principal projection) {
    return projection.owner(this);
  }

  @Override
  Principal project(TopPrincipal projection) {
    // Use the TopPrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  final Principal project(PrimitivePrincipal projection) {
    // Let p = this, and q = projection.
    Principal p = this;
    Principal q = projection;

    // Have p:q, which is normal form.
    return new OwnedPrincipal(p, q);
  }

  @Override
  final Principal project(ConfPrincipal projection) {
    // Use the ConfPrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  final Principal project(IntegPrincipal projection) {
    // Use the IntegPrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  final Principal project(OwnedPrincipal projection) {
    // Use the OwnedPrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  final Principal project(ConjunctivePrincipal projection) {
    // Use the ConjunctivePrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  final Principal project(DisjunctivePrincipal projection) {
    // Use the DisjunctivePrincipal owner implementation.
    return projection.owner(this);
  }

  @Override
  public final Principal owner(Principal owner) {
    return owner.project(this);
  }

  @Override
  Principal owner(TopPrincipal owner) {
    // Use the TopPrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(PrimitivePrincipal owner) {
    // Use the AbstractPrimitivePrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(ConfPrincipal owner) {
    // Use the ConfPrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(IntegPrincipal owner) {
    // Use the IntegPrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(OwnedPrincipal owner) {
    // Use the OwnedPrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(ConjunctivePrincipal owner) {
    // Use the ConjunctivePrincipal projection implementation.
    return owner.project(this);
  }

  @Override
  final Principal owner(DisjunctivePrincipal owner) {
    // Use the DisjunctivePrincipal projection implementation.
    return owner.project(this);
  }

  /**
   * Stores a new delegation with this principal.
   *
   * @param granter the principal granting privileges
   * @param superior the principal receiving new privileges
   * @param label the label on the new delegation
   */
  public final void addDelegatesTo(Principal granter, Principal superior,
      Principal label) {
    Set<DelegationPair> entry = delegations.get(label);
    if (entry == null) {
      entry = new HashSet<>();
      delegations.put(label, entry);
    }

    entry.add(new DelegationPair(granter, superior));
  }

  /**
   * Asks this principal whether it can find the (direct) delegation "{@code
   * superior} ≽ {@code granter}" whose label flows to {@code maxLabel}.
   * <p>
   * When making recursive calls, any principals receiving those calls must act
   * for {@code accessPolicy} and the integrity projection of {@code maxLabel}.
   * It is assumed that {@code accessPolicy} acts for the confidentiality
   * component of {@code maxLabel}. As such, no explicit checks will be made to
   * ensure that principals receiving recursive calls will act for this
   * component (by the above assumption, such a check would be subsumed by the
   * check against {@code accessPolicy}).
   * <p>
   * It is also assumed that {@code accessPolicy} has no integrity component.
   * <p>
   * A final assumption that is not explicitly checked is that this principal
   * acts for both {@code maxLabel} and {@code accessPolicy}. (Otherwise, we
   * have no business making this query to this principal!)
   * <p>
   * If {@code maxLabel} or {@code accessPolicy} is {@code null}, then a static
   * context is assumed (in which no dynamic delegations exists), and this
   * method returns false.
   *
   * @param granter the potential granter
   * @param superior the potential superior
   * @param maxLabel labels on delegations considered when satisfying this
   *          query must flow to this label
   * @param accessPolicy the confidentiality level of the query. This should
   *          act for the confidentiality component of {@code maxLabel}
   */
  public final boolean delegatesTo(Principal granter, Principal superior,
      Principal maxLabel, Principal accessPolicy) {
    return delegatesTo(new ActsForQuery<>(superior, granter, maxLabel,
        accessPolicy));
  }

  @Override
  Set<PrimitivePrincipal> componentPrimitivePrincipals() {
    return Collections.singleton(this);
  }

  @Override
  final Set<DelegationPair> usableDelegations(ActsForQuery<?, ?> query,
      ProofSearchState searchState) {
    if (!query.useDynamicContext()) {
      // Static context. No dynamic delegations should be used.
      return Collections.emptySet();
    }

    Set<DelegationPair> result = new HashSet<>();

    for (Map.Entry<Principal, Set<DelegationPair>> entry : delegations
        .entrySet()) {
      Principal delegationLabel = entry.getKey();
      Principal queryLabel = query.maxUsableLabel;

      // Can use delegations if delegationLabel ⊑ queryLabel.
      if (findActsForProof(ActsForQuery.flowsToQuery(delegationLabel,
          queryLabel, query.maxUsableLabel, query.accessPolicy), searchState) != null) {
        result.addAll(entry.getValue());
      }
    }

    return result;
  }

  @Override
  Set<Principal> askablePrincipals(ActsForQuery<?, ?> query,
      ProofSearchState searchState) {
    if (!query.useDynamicContext()) {
      // Static context. No dynamic delegations should be used.
      return Collections.emptySet();
    }

    Set<Principal> result = new HashSet<>();
    for (DelegationPair delegation : usableDelegations(query, searchState)) {
      result.addAll(delegation.inferior.componentPrimitivePrincipals());
      result.addAll(delegation.superior.componentPrimitivePrincipals());
    }

    return removeUnaskablePrincipals(result, query, searchState);
  }
}
