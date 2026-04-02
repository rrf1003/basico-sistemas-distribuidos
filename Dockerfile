# ==========================================
# Etapa 1: Compilación (Build)
# ==========================================
# Usamos una imagen oficial de Maven con Java
FROM maven:3-amazoncorretto-17

# Crear usuario nonroot
RUN groupadd -r nonroot && useradd -r -g nonroot -m nonroot \
    && mkdir -p /webapp \
    && chown -R nonroot:nonroot /webapp

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /webapp

# Copiar el código y compilar como root (para ecitar problemas de permisos en .m2)
COPY .. .

RUN mvn clean install

RUN chown -R nonroot:nonroot /webapp

USER nonroot

CMD ["mvn", "spring-boot:run"]

CMD mvn spring-boot:run

