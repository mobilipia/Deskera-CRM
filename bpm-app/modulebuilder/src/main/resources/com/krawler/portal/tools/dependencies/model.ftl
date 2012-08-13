package ${packagePath};

/**
 * <a href="${entity.getName()}.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>${entity.name}</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;
import java.util.Date;

public class ${entity.getName()} implements Serializable {
    <#list entity.getRegularColList() as column>
		private ${serviceBuilder.getPrimitiveObjClass("${column.getType()}")} ${column.getName()};
    </#list>
    <#list entity.getRegularColList() as column>
		<#if column.getName() == "classNameId">
			public String getClassName() {
				if (getClassNameId() <= 0) {
					return StringPool.BLANK;
				}

				return PortalUtil.getClassName(getClassNameId());
			}
		</#if>

		public ${serviceBuilder.getPrimitiveObjClass("${column.getType()}")} get${column.getMethodName()}() {
            <#setting datetime_format="yyyy-MM-dd HH:mm:ss">
            <#if column.getDefault() != "">
                if(this.${column.getName()} == null) {
                <#if column.getType() == "double">
                    <#setting number_format="0.0###">
                <#else>
                    <#setting number_format="#">
                </#if>
                <#if column.getType() == "Date">
                    try{
                        java.text.DateFormat dfm = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        this.date = (Date) dfm.parse(${serviceBuilder.getPrimitiveObjValue("${column.getType()}", "${column.getDefault()}")});
                    }catch(Exception e){
                        this.date = (Date) new Date();
                    }
                <#else>
                    this.${column.getName()}=(${serviceBuilder.getPrimitiveObjClass("${column.getType()}")}) ${serviceBuilder.getPrimitiveObjValue("${column.getType()}", "${column.getDefault()}")};
                </#if>
                }
                return this.${column.getName()};
			<#elseif column.getType() == "String" && column.isConvertNull()>
                if(this.${column.getName()} == null) {
                      this.${column.getName()}="";
                }
                return this.${column.getName()};
            <#else>
				return this.${column.getName()};
			</#if>
		}

    	<#if column.getType()== "boolean">
			public ${serviceBuilder.getPrimitiveObjClass("${column.getType()}")} is${column.getMethodName()}() {
				return ${column.getName()};
			}
		</#if>

		public void set${column.getMethodName()}(${serviceBuilder.getPrimitiveObjClass("${column.getType()}")} ${column.getName()}) {
			<#if column.getName() == "uuid">
				if ((uuid != null) && (uuid != this.uuid)) {
					uuid = uuid;
				}
			<#else>
				if (

				<#if column.isPrimitiveType()>
					${column.getName()} != this.${column.getName()}
				<#else>
					(${column.getName()} == null && this.${column.getName()} != null) ||
					(${column.getName()} != null && this.${column.getName()} == null) ||
					(${column.getName()} != null && this.${column.getName()} != null && !${column.getName()}.equals(this.${column.getName()}))
				</#if>

				) {
					this.${column.getName()} = ${column.getName()};
				}
			</#if>
		}
	</#list>
}
