package com.bakdata.conquery.models.auth.subjects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.bakdata.conquery.io.xodus.MasterMetaStorage;
import com.bakdata.conquery.models.auth.permissions.ConqueryPermission;
import com.bakdata.conquery.models.auth.util.SinglePrincipalCollection;
import com.bakdata.conquery.models.exceptions.JSONException;
import com.bakdata.conquery.models.identifiable.Labeled;
import com.bakdata.conquery.models.identifiable.ids.specific.PermissionOwnerId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@JsonIgnoreProperties({"session", "previousPrincipals", "runAs", "principal", "authenticated", "remembered"})
public abstract class PermissionOwner<T extends PermissionOwnerId<? extends PermissionOwner<T>>> extends Labeled<T> implements  Subject {
	
	@Getter
	private final SinglePrincipalCollection principals;
	
	@Getter
	private transient final Set<ConqueryPermission> permissions = new HashSet<>();
	@Getter @Setter
	private transient MasterMetaStorage storage;

	@Override
	public Object getPrincipal() {
		return principals.getPrimaryPrincipal();
	}
	
	@Override
	public boolean isPermitted(String permission) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean[] isPermitted(String... permissions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPermittedAll(String... permissions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkPermission(String permission) throws AuthorizationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkPermissions(String... permissions) throws AuthorizationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRole(String roleIdentifier) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean[] hasRoles(List<String> roleIdentifiers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasAllRoles(Collection<String> roleIdentifiers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkRole(String roleIdentifier) throws AuthorizationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkRoles(Collection<String> roleIdentifiers) throws AuthorizationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkRoles(String... roleIdentifiers) throws AuthorizationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void login(AuthenticationToken token) throws AuthenticationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logout() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> V execute(Callable<V> callable) throws ExecutionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void execute(Runnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> Callable<V> associateWith(Callable<V> callable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Runnable associateWith(Runnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void runAs(PrincipalCollection principals) throws NullPointerException, IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRunAs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrincipalCollection getPreviousPrincipals() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrincipalCollection releaseRunAs() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isAuthenticated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRemembered() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * For keeping local permissions in sync with stored permissions.
	 * Is used by the storage.
	 */
	public void addPermissionLocal(ConqueryPermission permission) {
		ConqueryPermission ownedPermission = permission;
		if(!permission.getOwnerId().equals(this.getId())) {
			ownedPermission = permission.withOwner(this.getId());
		}
		permissions.add(ownedPermission);
	}

	/**
	 * For keeping local permissions in sync with stored permissions.
	 * Is used by the storage.
	 */
	public void removePermissionLocal(ConqueryPermission permission) {
		permissions.remove(permission);
	}
	
	/**
	 * Adds a permission to the storage and to the locally stored permissions by calling
	 * indirectly {@link #addPermissionLocal(ConqueryPermission)}.
	 *
	 * @return Returns the added Permission (Id might changed when the owner changed or
	 * permissions are aggregated
	 */
	public ConqueryPermission addPermission(ConqueryPermission permission) throws JSONException{
		ConqueryPermission ownedPermission = permission;
		if(!permission.getOwnerId().equals(this.getId())) {
			ownedPermission = permission.withOwner(this.getId());
		}
		
		Optional<ConqueryPermission> sameTarget = ofTarget(ownedPermission);
		
		if(sameTarget.isPresent()) {
			// found permission with the same target
			if(sameTarget.get().equals(ownedPermission)) {
				// is actually the same permission
				log.info("User {} has already permission {}.", this, ownedPermission);
				return ownedPermission;
			} else {
				// new permission has different ability
				ConqueryPermission oldPermission = sameTarget.get();
				List<ConqueryPermission> reducedPermission = ConqueryPermission.reduceByOwnerAndTarget(Arrays.asList(oldPermission, ownedPermission));
				storage.removePermission(oldPermission.getId());
				// has only one entry as permissions only differ in the ability
				storage.addPermission(reducedPermission.get(0));
				return reducedPermission.get(0);
			}
		}
		// first permission with the provided Target
		storage.addPermission(ownedPermission);
		return ownedPermission;
	}
	
	/**
	 * Removes a permission from the storage and from the locally stored permissions by calling
	 * indirectly {@link #removePermissionLocal(ConqueryPermission)}.
	 */
	public void removePermission(ConqueryPermission permission) {
		storage.removePermission(permission.getId());
	}
	
	private Optional<ConqueryPermission> ofTarget(Object obj) {
		if(!(obj instanceof ConqueryPermission)) {
			return Optional.empty();
		}
		ConqueryPermission other = (ConqueryPermission) obj;
		Iterator<ConqueryPermission> it = permissions.iterator();
		while(it.hasNext()) {
			ConqueryPermission perm = it.next();
			if(perm.getTarget().equals(other.getTarget())) {
				return Optional.of(perm);
			}
		}
		return Optional.empty();
		
	}
	
	@Override
	public String getLabel() {
		String ret = super.getLabel();
		if(ret == null) {
			ret = getId().toString();
		}
		return ret;
	}
	
	/**
	 * Owns the permission and checks if it is permitted by only regarding owner specific permissions.
	 * Inherit permission from roles are not checked.
	 */
	public boolean isPermittedSelfOnly(ConqueryPermission permission) {
		return  SecurityUtils.getSecurityManager().isPermitted(getPrincipals(), permission);
	}

}
