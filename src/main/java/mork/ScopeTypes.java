package mork;

/**
 * Mork specifies scopes. Nearly all elements can optionally be scoped into
 * different categories, so they are separated and can be stored within the same
 * document coexisting without their existence of each other.
 * 
 * Mork already specifies some scopes, but we use only the two found in real
 * files: the atom scope and the column scope.
 * 
 * @author mhaller
 */
public enum ScopeTypes {
	/** Values are usually in the Atom Scope */
	ATOM_SCOPE,

	/** Column Names or Cell Ids are in the Column Scope */
	COLUMN_SCOPE
}
