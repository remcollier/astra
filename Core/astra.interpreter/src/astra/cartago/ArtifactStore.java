package astra.cartago;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import astra.formula.Predicate;
import cartago.ArtifactId;

public class ArtifactStore {
    private Map<String, Predicate> observableProperties = new HashMap<String, Predicate>();
    private Map<String, Set<String>> artifactProperties = new HashMap<String, Set<String>>();
    
    public void storeObservableProperty( ArtifactId aid, String identifier, Predicate property ) {
        observableProperties.put( identifier, property );
        if ( aid != null ) {
        	String id = aid.getName();
        	Set<String> props = artifactProperties.get( id );
        	if ( props == null ) {
        		props = new HashSet<String>();
        		artifactProperties.put( id,  props );
        	}
        	
        	if ( !props.contains( id ) ) {
        		props.add( identifier );
        	}
        }
    }
    
    public void removeObservableProperty( ArtifactId aid, String identifier ) { 
    	observableProperties.remove( identifier );

    	if ( aid != null ) {
        	String id = aid.getName();
        	Set<String> props = artifactProperties.get( id );
        	if ( props != null ) {
        		props.remove( identifier );
        	}
        }
    }
    
    public Predicate getObservableProperty( String identifier ) {
        for ( Entry<String, Predicate> entry : observableProperties.entrySet() ) {
            if ( entry.getValue().predicate().equals( identifier ) ) {
                return entry.getValue();
            }
        }

        return null;
    }

	public Predicate getObservableProperty(Object value, String predicate) {
		if (value instanceof String) {
			Set<String> props = artifactProperties.get( value );
			if ( props != null ) {
				for ( String prop : props ) {
					Predicate property = observableProperties.get( prop );
					if ( property.predicate().equals( predicate ) ) {
						return property;
					}
				}
			}
			return observableProperties.get(value);
		} else if (value instanceof ArtifactId) {
			Set<String> props = artifactProperties.get( ((ArtifactId) value).getName() );
			if ( props != null ) {
				for ( String prop : props ) {
					Predicate property = observableProperties.get( prop );
					if ( property.predicate().equals( predicate ) ) {
						return property;
					}
				}
			}
		}
		return null;
	}

	public void removeArtifactProperties(ArtifactId artifactId) {
		artifactProperties.remove(artifactId.getName());
	}
}
