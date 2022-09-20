package dsenta.cachito.mapper.attribute;

import dsenta.cachito.exception.resource.ResourceNotFoundException;
import dsenta.cachito.exception.attribute.UnsupportedDataTypeException;
import dsenta.cachito.mapper.objectinstance.BooleanMapper;
import dsenta.cachito.mapper.objectinstance.FloatMapper;
import dsenta.cachito.mapper.objectinstance.IntMapper;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.AttributeSave;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.schema.Schema;
import dsenta.cachito.utils.DateIso8601;
import lombok.NoArgsConstructor;

import static dsenta.cachito.assertions.attribute.AttributeAssert.NOW;
import static dsenta.cachito.constants.attribute.DataTypeConstants.*;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AttributeMapper {

    public static Attribute fromDto(Schema schema, AttributeSave attributeSave) {
        var attribute = new Attribute();
        attribute.setName(attributeSave.getName());
        attribute.setCascadeDelete(attributeSave.isCascadeDelete());
        attribute.setCascadePersist(attributeSave.isCascadePersist());
        attribute.setUnique(attributeSave.isUnique());
        attribute.setFilterable(attributeSave.isFilterable());
        parseDataType(attribute, schema, attributeSave);

        return attribute;
    }

    private static void parseDataType(Attribute attribute,
                                      Schema schema,
                                      AttributeSave attributeSave) {
        var defaultValue = attributeSave.getDefaultValue();
        var dataTypeTokens = attributeSave.getType().split("\\s+");
        var dataType = dataTypeTokens[0].toLowerCase();
        attribute.setDataType(DataType.fromString(dataType));

        switch (dataType) {
            case INT:
            case INTEGER:
            case LONG:
                attribute.setDefaultValue(IntMapper.toLongInt(defaultValue));
                break;
            case FLOAT:
            case DOUBLE:
                attribute.setDefaultValue(FloatMapper.toDouble(defaultValue));
                break;
            case STRING:
                attribute.setDefaultValue(defaultValue);
                break;
            case DATE:
                if (defaultValue instanceof String) {
                    var defaultValueAsString = String.valueOf(defaultValue);
                    if (NOW.equalsIgnoreCase(defaultValueAsString)) {
                        attribute.setDefaultValue(defaultValue);
                    } else {
                        attribute.setDefaultValue(DateIso8601.fromString(String.valueOf(defaultValue)));
                    }
                } else {
                    attribute.setDefaultValue(defaultValue);
                }
                break;
            case BOOL:
            case BOOLEAN:
                attribute.setDefaultValue(BooleanMapper.toBoolean(defaultValue));
                break;
            case ZEROONE:
            case ZEROMANY:
            case ONE:
            case ONEMANY:
                String relatedResourceName = dataTypeTokens[1];
                if (!schema.getClazzMap().containsKey(relatedResourceName)) {
                    throw new ResourceNotFoundException(relatedResourceName, schema);
                }

                var relatedResource = schema.getClazzMap().get(relatedResourceName);
                attribute.setClazz(relatedResource);

                if (nonNull(attributeSave.getInverseBy())) {
                    attribute.setInverseBy(attributeSave.getInverseBy());
                    Attribute inverseAttribute = relatedResource.getAttributeRecursive(attributeSave.getInverseBy());
                    inverseAttribute.setInverseBy(attribute.getName());
                }
                break;
            default:
                throw new UnsupportedDataTypeException(attributeSave.getType());
        }
    }
}