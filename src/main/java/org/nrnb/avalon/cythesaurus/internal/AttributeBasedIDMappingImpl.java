package org.nrnb.avalon.cythesaurus.internal;

/*******************************************************************************
 * Copyright 2010-2013 CyThesaurus developing team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import org.nrnb.avalon.cythesaurus.internal.util.DataSourceWrapper;
import org.nrnb.avalon.cythesaurus.internal.util.IDMapperWrapper;
import org.nrnb.avalon.cythesaurus.internal.util.XrefWrapper;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import org.cytoscape.work.TaskMonitor;

import giny.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class AttributeBasedIDMappingImpl implements AttributeBasedIDMapping{
    protected TaskMonitor taskMonitor;
    protected boolean interrupted;
    protected String report;
    protected Map<String,Byte> attrNameType = null;

    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
        interrupted = false;
    }

    public void interrupt() {
            interrupted = true;
            report = "Aborted!";
     }

    public String getReport() {
        return report;
    }

    /**
     * Define target attributes.
     * Call this method first before mapping if necessary.
     * @param attrNameType
     */
    public void suggestTgtAttrType(CyNetwork network, Map<String,Byte> attrNameType) {
        this.attrNameType = attrNameType;
        
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        MultiHashMapDefinition mmapDef = nodeAttributes.getMultiHashMapDefinition();

        for (Map.Entry<String,Byte> entry : attrNameType.entrySet()) {
            String attrname = entry.getKey();
            byte attrtype = entry.getValue();
            
            //hack to allow overwriting existing attributes
            boolean skip = false;
            for (String existingName : nodeAttributes.getAttributeNames()){
            	if (attrname.equals(existingName)){
            		skip = true;
            	}
            }
            if(skip)
            	break;

            byte[] keyTypes;
            if (attrtype==CyAttributes.TYPE_STRING) {
                    keyTypes = null;
            } else if (attrtype==CyAttributes.TYPE_SIMPLE_LIST ) {
                    keyTypes = new byte[] { MultiHashMapDefinition.TYPE_INTEGER };
            } else {
                    keyTypes = null;
            }

            mmapDef.defineAttribute(attrname,
                                    MultiHashMapDefinition.TYPE_STRING,
                                    keyTypes);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void map(Map<String,Set<DataSourceWrapper>> mapSrcAttrIDTypes,
            Map<String, DataSourceWrapper> mapTgtAttrNameIDType) {

        // prepare source xrefs
        Set<Node> nodes = nodesUnion(networks);
        Map<Node,Set<XrefWrapper>> mapNodeSrcXrefs = prepareNodeSrcXrefs(nodes, mapSrcAttrIDTypes);
        Set<XrefWrapper> srcXrefs = srcXrefUnion(mapNodeSrcXrefs);

        // target id types
        Set<DataSourceWrapper> tgtTypes = new HashSet(mapTgtAttrNameIDType.values());

        // id mapping
        updateTaskMonitor("Mapping IDs...",-1.0);
        Map<XrefWrapper, Set<XrefWrapper>> mapping = IDMapperWrapper.mapID(srcXrefs, tgtTypes);

        // define target attribute
        defineTgtAttributes(mapTgtAttrNameIDType.keySet());

        // set target attribute
        Map<Node,Set<XrefWrapper>> mapNodeTgtXrefs = getNodeTgtXrefs(mapNodeSrcXrefs, mapping);
        setTgtAttribute(mapNodeTgtXrefs, mapTgtAttrNameIDType);

        report = "Identifiers mapped for "+mapNodeTgtXrefs.size()+" nodes (out of "+nodes.size()+")!";
        updateTaskMonitor(report,1.00);
    }

//    private Set<Node> nodesUnion(Set<CyNetwork> networks) {
//        int nNode = 0;
//        for (CyNetwork network : networks) {
//            nNode += network.getNodeCount();
//        }
//
//        Set nodes = new HashSet();
//
//        int i = 0;
//        for (CyNetwork network : networks) {
//			for (Iterator<Node> nodeIt = network.nodesIterator(); nodeIt.hasNext();) {
//                if (interrupted) return null;
//                updateTaskMonitor("Retrieving nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
//                i++;
//
//				nodes.add(nodeIt.next());
//			}
//		}
//        return nodes;
//    }
//
//    private Map<Node,Set<XrefWrapper>> prepareNodeSrcXrefs(Set<Node> nodes, Map<String,Set<DataSourceWrapper>> mapSrcAttrIDTypes) {
//        Map<Node,Set<XrefWrapper>> ret = new HashMap();
//        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
//
//        int nNode = nodes.size();
//        int i=0;
//        for (Node node : nodes) {
//            if (interrupted) return null;
//            updateTaskMonitor("Preparing cross reference for nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
//            i++;
//
//            Set<XrefWrapper> xrefs = new HashSet();
//            ret.put(node, xrefs);
//
//            String nodeID = node.getIdentifier();
//            for (Map.Entry<String,Set<DataSourceWrapper>> entryAttrIDTypes : mapSrcAttrIDTypes.entrySet()) {
//                String attrName = entryAttrIDTypes.getKey();
//                Set<DataSourceWrapper> dss = entryAttrIDTypes.getValue();
//
//                //TODO: remove this in Cy3
//                if (attrName.compareTo("ID")==0) {
//                    for (DataSourceWrapper ds : dss) {
//                        xrefs.add(new XrefWrapper(nodeID, ds));
//                    }
//                    continue;
//                }
//
//                byte attrType = nodeAttributes.getType(attrName);
//                if (attrType == CyAttributes.TYPE_SIMPLE_LIST) {
//                    List attr = nodeAttributes.getListAttribute(nodeID, attrName);
//                    for (Object obj : attr) {
//                        String str = obj.toString();
//                        for (DataSourceWrapper ds : dss) {
//                            xrefs.add(new XrefWrapper(str, ds));
//                        }
//                    }
//                } else {
//                    Object obj = nodeAttributes.getAttribute(nodeID, attrName);
//                    if (obj!=null) {
//                        String str = obj.toString();
//                        if (str.length()>0) {
//                            for (DataSourceWrapper ds : dss) {
//                                xrefs.add(new XrefWrapper(str, ds));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return ret;
//    }
//
//    private Set<XrefWrapper> srcXrefUnion(Map<Node,Set<XrefWrapper>> mapNodeSrcXrefs) {
//        Set<XrefWrapper> ret = new HashSet();
//        for (Set<XrefWrapper> xrefs : mapNodeSrcXrefs.values()) {
//            ret.addAll(xrefs);
//        }
//        return ret;
//    }
//
//    private Map<Node,Set<XrefWrapper>> getNodeTgtXrefs (Map<Node,Set<XrefWrapper>> mapNodeSrcXrefs,
//                                                 Map<XrefWrapper, Set<XrefWrapper>> idMapping) {
//        Map<Node,Set<XrefWrapper>> mapNodeTgtXrefs = new HashMap();
//
//        for (Map.Entry<Node,Set<XrefWrapper>> entryNodeXrefs : mapNodeSrcXrefs.entrySet()) {
//            Node node = entryNodeXrefs.getKey();
//            Set<XrefWrapper> tgtXrefs = new HashSet();
//            Set<XrefWrapper> srcXrefs = entryNodeXrefs.getValue();
//            //TODO: deal with ambiguity--same node, same attribute, different data source
//            for (XrefWrapper srcXref : srcXrefs) {
//                Set<XrefWrapper> xrefs = idMapping.get(srcXref);
//                if (xrefs!=null) {
//                    tgtXrefs.addAll(xrefs);
//                }
//            }
//
//            if (!tgtXrefs.isEmpty())
//                mapNodeTgtXrefs.put(node, tgtXrefs);
//        }
//
//        return mapNodeTgtXrefs;
//    }
//
//    private void defineTgtAttributes(Set<String> attrs) {
//        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
//        List<String> attrNames = java.util.Arrays.asList(nodeAttributes.getAttributeNames());
//
//        MultiHashMapDefinition mmapDef = nodeAttributes.getMultiHashMapDefinition();
//
//        for (String attr : attrs) {
//            if (attrNames.contains(attr)) {
//                // for existing attribute, check if its type is String or List
//                byte attrType = nodeAttributes.getType(attr);
//                if (attrType!=CyAttributes.TYPE_STRING && attrType!=CyAttributes.TYPE_SIMPLE_LIST) {
//                    throw new java.lang.UnsupportedOperationException("Only String and List target attributes are supported.");
//                }
//            } else {
//                // define the new attribute as List
//                // only if it is explicitly defined as String, o.w. default is list of string
//                byte[] keyTypes;
//                if (attrNameType!=null && attrNameType.get(attr)==CyAttributes.TYPE_STRING) {
//                    keyTypes = null;
//                }
//
//                keyTypes = new byte[] { MultiHashMapDefinition.TYPE_INTEGER };
//                mmapDef.defineAttribute(attr,
//                                    MultiHashMapDefinition.TYPE_STRING,
//                                    keyTypes);
//            }
//        }
//    }
//
//    private void setTgtAttribute(Map<Node,Set<XrefWrapper>> mapNodeTgtXrefs,
//                                 Map<String, DataSourceWrapper> mapTgtAttrNameIDType) {
//        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
//        Map<String, Byte> mapAttrNameType = new HashMap();
//        Map<DataSourceWrapper, Set<String>> mapIDTypeAttrName = new HashMap();
//        for (String attrName : mapTgtAttrNameIDType.keySet()) {
//            mapAttrNameType.put(attrName, nodeAttributes.getType(attrName));
//
//            DataSourceWrapper idType = mapTgtAttrNameIDType.get(attrName);
//            Set<String> names = mapIDTypeAttrName.get(idType);
//            if (names==null) {
//                names = new HashSet();
//                mapIDTypeAttrName.put(idType, names);
//            }
//            names.add(attrName);
//        }
//
//        int i = 0;
//        int nNode = mapNodeTgtXrefs.size();
//        for (Map.Entry<Node,Set<XrefWrapper>> entryNodeXrefs : mapNodeTgtXrefs.entrySet()) {
//            if (interrupted) return;
//            updateTaskMonitor("Preparing cross reference for nodes...\n"+i+"/"+nNode,(i+1)*100/nNode);
//            i++;
//
//            // type wise
//            Map<DataSourceWrapper, Set<String>> mapDsIds = new HashMap();
//            Set<XrefWrapper> tgtXrefs = entryNodeXrefs.getValue();
//            for (XrefWrapper xref : tgtXrefs) {
//                DataSourceWrapper ds = xref.getDataSource();
//                Set<String> ids = mapDsIds.get(ds);
//                if (ids==null) {
//                    ids = new TreeSet(); // alphabetically
//                    mapDsIds.put(ds, ids);
//                }
//                ids.add(xref.getValue());
//            }
//            
//            // set attribute
//            Node node = entryNodeXrefs.getKey();
//            String nodeID = node.getIdentifier();
//            for (Map.Entry<DataSourceWrapper, Set<String>> entryDsIds : mapDsIds.entrySet()) {
//                DataSourceWrapper ds = entryDsIds.getKey();
//                Set<String> attrNames = mapIDTypeAttrName.get(ds);
//                if (attrNames==null) {
//                    // TODO: what happened?
//                    continue;
//                }
//                for (String attrName : attrNames) {
//                    byte attrType = mapAttrNameType.get(attrName);
//                    Set<String> ids = entryDsIds.getValue();
//                    if (attrType==CyAttributes.TYPE_SIMPLE_LIST) {
//                        List<String> values = new Vector(ids);
//                        nodeAttributes.setListAttribute(nodeID, attrName, values);
//                    } else if (attrType==CyAttributes.TYPE_STRING) {
//                        // only returns the first ID
//                        //TODO: is that a way to get the "best" one?
//                        if (!ids.isEmpty()) {
//                            nodeAttributes.setAttribute(nodeID, attrName, ids.iterator().next());
//                        }
//                    }
//                }
//            }
//
//        }
//
//    }

    private void updateTaskMonitor(String status, double percentage) {
        if (this.taskMonitor!=null) {
            taskMonitor.setStatusMessage(status);
            taskMonitor.setProgress(percentage);
        }
    }

    private void updateTaskMonitor(String status) {
        if (this.taskMonitor!=null) {
            taskMonitor.setStatusMessage(status);
        }
    }
}
