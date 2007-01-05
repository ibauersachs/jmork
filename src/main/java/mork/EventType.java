package mork;

public enum EventType {
	// < open angle - begins a dict (inside a dict, begins metainfo row)
	// > close angle - ends a dict
	// [ open bracket - begins a row (inside a row, begins metainfo row)
	// ] close bracket - ends a row
	// { open brace - begins a table (inside a table, begins metainfo row)
	// } close brace - ends a table
	// ( open paren - begins a cell
	// ) close paren - ends a cell
	// ^ up arrow - dereference following id for literal value
	// r lower r - dereference following oid for row (by ref) value
	// t lower t - dereference following oid for table (by ref) value
	// : colon - next value is scope namespace for preceding id
	// = equals - begin a literal value inside a cell
	// + plus - add update: insert content
	// - minus - cut update: remove content
	// ! bang - put update: clear and set content

	/**
	 * A '<' begint the Dict. If already in a dict, another event is fired:
	 * BEGIN_DICT_METAINFO
	 */
	BEGIN_DICT,

	/** '>' Close Angle ends a dict */
	END_DICT,

	/** A '<' inside a Dict starts the Dict's Metainfo Part */
	BEGIN_DICT_METAINFO,

	/** Ends the Metainfo within a Dict */
	END_DICT_METAINFO,

	/** '(' Open Parenthesis */
	BEGIN_CELL,

	/** ')' Close Parent ends a cell */
	END_CELL,

	/** Special Event Type: an ID Value appeared */
	ID,

	/** '=' Begin A Literal Value Inside A Cell */
	BEGIN_VALUE,

	/** A Name of an Identifier */
	NAME,

	/** End of Literal Event */
	END_LITERAL,

	/** A Literal */
	LITERAL,

	/** Comment */
	COMMENT,

	/** End of File */
	END_OF_FILE,

	/**
	 * A committed transaction group. The value property of the Event will be
	 * the whole content of the transaction group in Mork grammar.
	 */
	GROUP_COMMIT,

	/**
	 * A transaction group which has been rolled-back. The value property of the
	 * Event will be the transaction id.
	 */
	GROUP_ABORT,

	/** A table */
	TABLE,

	/** A cell */
	CELL,

	/** A row */
	ROW,

	/** The end of a row */
	END_OF_ROW,

	/** End of table */
	END_TABLE,

	/** Begin a table */
	BEGIN_TABLE,

	/** A meta table within a table contains only cells */
	BEGIN_METATABLE,

	/** A meta table within a table contains only cells */
	END_METATABLE

}
