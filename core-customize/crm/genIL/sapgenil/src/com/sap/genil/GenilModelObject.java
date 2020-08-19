/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */

package com.sap.genil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * This class represents the GenIL object which belongs to GenIL model. <br>
 *
 * @author SAP
 * @version 1.0
 * 
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilModelObject
{


	/**
	 * The name of the GenIL object
	 */
	private String objectName;

	/**
	 * Kind of the Object
	 */
	private final GenilModelObjectKind objectKind;

	/**
	 * This Map stores relation which points to the parent object. Key is the relation name.
	 */
	private final Map<String, GenilModelRelation> parentRelation;

	/**
	 * This Map stores all relations which point to children of current object. Key is the relation name.
	 */
	private final Map<String, GenilModelRelation> childRelations;

	/**
	 * This Map stores all attributes that belong to the current object. Key is the attribute name.
	 */
	private final Map<String, GenilModelAttr> attrsByName;

	/**
	 * This Map stores all attributes that belong to the current object. Key is the attribute index.
	 */
	private final Map<Integer, GenilModelAttr> attrsByIndex;

	private final Map<String, GenilModelMethod> methods;

	private final Map<String, GenilModelAttrOption> attributeDefaultOptions;

	protected GenilModelObject(final String objectName, final GenilModelObjectKind objectKind)
	{
		super();

		this.objectName = objectName;
		this.objectKind = objectKind;

		// init HashMaps
		this.parentRelation = new ConcurrentHashMap<String, GenilModelRelation>();
		this.childRelations = new ConcurrentHashMap<String, GenilModelRelation>();
		this.attrsByIndex = new ConcurrentHashMap<Integer, GenilModelAttr>();
		this.attrsByName = new ConcurrentHashMap<String, GenilModelAttr>();
		this.methods = new ConcurrentHashMap<String, GenilModelMethod>();
		this.attributeDefaultOptions = new ConcurrentHashMap<String, GenilModelAttrOption>();

	}

	public Map<String, GenilModelAttrOption> getAttributeDefaultOptions()
	{
		return attributeDefaultOptions;
	}

	protected void addAttributeDefaultOption(final char attributeSign, final String attributeOption)
	{
		final String attibuteKey = attributeSign + attributeOption;
		final GenilModelAttrOption attributeDefaultOption = new GenilModelAttrOption(attributeSign, attributeOption);
		this.attributeDefaultOptions.put(attibuteKey, attributeDefaultOption);
	}

	protected void addAttributeDefaultOption(final GenilModelAttrOption attributeDefaultOption)
	{
		final String attibuteKey = attributeDefaultOption.getModelAttrSign() + attributeDefaultOption.getModelAttrOption();
		this.attributeDefaultOptions.put(attibuteKey, attributeDefaultOption);
	}

	protected void addAttribute(final GenilModelAttr attr)
	{
		this.attrsByIndex.put(attr.getStructIndex(), attr);
		this.attrsByName.put(attr.getName(), attr);
	}

	protected void addChildRelation(final GenilModelRelation rel)
	{
		childRelations.put(rel.getRelation(), rel);
	}

	protected void addMethod(final GenilModelMethod method)
	{
		methods.put(method.getMethodName(), method);
	}

	protected void addParentRelation(final GenilModelRelation rel)
	{
		parentRelation.put(rel.getRelation(), rel);
	}

	/**
	 * Returns all Relation Objects that are direct reachable via this object<br>
	 *
	 * @return An unmodifiable collection containing all child relation objects
	 */
	public Collection<GenilModelRelation> getAllChildRelations()
	{
		return Collections.unmodifiableCollection(childRelations.values());
	}

	public GenilModelAttr getAttrByIndex(final int index)
	{
		return attrsByIndex.get(index);
	}

	public GenilModelAttr getAttrByName(final String name)
	{
		return attrsByName.get(name);
	}

	public GenilModelMethod getMethod(final String methodName)
	{
		return methods.get(methodName);
	}

	public GenilModelObjectKind getObjectKind()
	{
		return objectKind;
	}

	public String getObjectName()
	{
		return objectName;
	}

	public GenilModelRelation getParentRel(final String relName)
	{
		return this.parentRelation.get(relName);
	}

	/**
	 * Returns the relations from this objects to its child objects
	 *
	 * @param relationName
	 *           Name of the relation to obtain
	 * @return returns the relation Object with the given name, or null if such an relation does not exist.
	 */
	public GenilModelRelation getRelation(final String relationName)
	{
		return childRelations.get(relationName);
	}

	protected void setObjectName(final String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return objectKind + " - " + objectName;
	}

}
