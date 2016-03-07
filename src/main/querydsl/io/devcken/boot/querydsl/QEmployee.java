package io.devcken.boot.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEmployee is a Querydsl query type for QEmployee
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QEmployee extends com.querydsl.sql.RelationalPathBase<QEmployee> {

    private static final long serialVersionUID = -2042217006;

    public static final QEmployee Employee = new QEmployee("Employee");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QEmployee> primary = createPrimaryKey(id);

    public QEmployee(String variable) {
        super(QEmployee.class, forVariable(variable), "null", "Employee");
        addMetadata();
    }

    public QEmployee(String variable, String schema, String table) {
        super(QEmployee.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEmployee(Path<? extends QEmployee> path) {
        super(path.getType(), path.getMetadata(), "null", "Employee");
        addMetadata();
    }

    public QEmployee(PathMetadata metadata) {
        super(QEmployee.class, metadata, "null", "Employee");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

