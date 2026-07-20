/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.util;

/**
 *
 * @author root
 */
public class Constant {
	public static final int CREATE = 1;
	public static final int MODIFY = 2;
	public static final int REMOVE = 3;
	public static final int VIEW = 4;
	public static final int Search = 5;
	public static final int LOGIN = 6;

	private Constant() {
	}

	public static String getActionicon(int id) {
		switch (id) {
		case 1:
			return "<span title=\"ADD\" class=\"glyphicon glyphicon-plus\"></span> ADD";
		case 2:
			return "<span title=\"EDIT\" class=\"glyphicon glyphicon-pencil\"></span> EDIT";
		case 3:
			return "<span title=\"REMOVE\" class=\"glyphicon glyphicon-trash\"></span> REMOVE";
		case 4:
			return "<span title=\"VIEW\" class=\"glyphicon glyphicon-eye-open\"> VIEW";
		case 5:
			return "<span title=\"SEARCH\" class=\"glyphicon glyphicon-search\"> SEARCH";
		case 6:
			return "<span title=\"LOGIN\" class=\"glyphicon glyphicon-log-in\"> LOGIN";
		default:
			return "<span title=\"OTHER\" class=\"glyphicon glyphicon-option-horizontal\"> OTHER";
		}

	}

	public static String getAction(int id) {
		switch (id) {
		case 1:
			return "ADD";
		case 2:
			return "EDIT";
		case 3:
			return "REMOV";
		case 4:
			return "VIEW";
		case 5:
			return "SEARCH";
		case 6:
			return "LOGIN";
		default:
			return "OTHER";
		}

	}

}
