"""Afiche HotelNova 90 x 120 cm a partir de la plantilla UTS (Formato_Proyecto_de_Investigación_IA).

Conserva el fondo de la plantilla (logos UTS y Nodo Santander, marco verde y pie de la Dirección de
Investigaciones), las cajas con encabezado verde, el recuadro de ponentes con sus iconos y el título.
Todo el texto va en Arial para que PowerPoint y el PDF de impresión se vean igual.
"""
import copy
import math
import sys

from lxml import etree
from pptx import Presentation
from pptx.util import Emu
from PIL import Image

CM = 360000
NS = {'a': 'http://schemas.openxmlformats.org/drawingml/2006/main',
      'p': 'http://schemas.openxmlformats.org/presentationml/2006/main'}
A = '{%s}' % NS['a']
FUENTE = 'Arial'

ENT, SAL = sys.argv[1], sys.argv[2]
prs = Presentation(ENT)
sl = prs.slides[0]
arbol = sl.shapes._spTree


def buscar(nombre):
    for sh in sl.shapes:
        if sh.name == nombre:
            return sh
        if sh.shape_type == 6:
            for c in sh.shapes:
                if c.name == nombre:
                    return c
    raise KeyError(nombre)


# moldes de la plantilla: encabezado verde y caja blanca del cuerpo
MOLDE_ENC = copy.deepcopy(buscar('CuadroTexto 5')._element)
MOLDE_CUERPO = copy.deepcopy(buscar('CuadroTexto 35')._element)

# fuera lo que es de la plantilla y no de HotelNova (grupos de contenido, logo del semillero, captura de ejemplo)
for sh in list(sl.shapes):
    if sh.shape_type == 6 or sh.name in ('CuadroTexto 28', 'CuadroTexto 2', 'CuadroTexto 51', 'Imagen 11', 'Imagen 52'):
        arbol.remove(sh._element)

_id = [500]


def nuevo_id(el, nombre):
    _id[0] += 1
    c = el.find('.//p:cNvPr', NS)
    c.set('id', str(_id[0]))
    c.set('name', nombre)
    for ext in c.findall('a:extLst', NS):
        c.remove(ext)


def posicion(el, x, y, w, h):
    off = el.find('.//a:xfrm/a:off', NS)
    ext = el.find('.//a:xfrm/a:ext', NS)
    off.set('x', str(int(x * CM)))
    off.set('y', str(int(y * CM)))
    ext.set('cx', str(int(w * CM)))
    ext.set('cy', str(int(h * CM)))


def rpr(sz, b=False, color=None):
    r = etree.SubElement(etree.Element(A + 'dummy'), A + 'rPr', lang='es-CO', sz=str(sz), dirty='0')
    if b:
        r.set('b', '1')
    else:
        r.set('b', '0')
    if color:
        f = etree.SubElement(r, A + 'solidFill')
        etree.SubElement(f, A + 'srgbClr', val=color)
    etree.SubElement(r, A + 'latin', typeface=FUENTE)
    etree.SubElement(r, A + 'cs', typeface=FUENTE)
    return r


def parrafo(partes, sz, algn='just', vineta=False, antes=0, despues=600):
    """partes = [(texto, negrita)]"""
    p = etree.Element(A + 'p')
    ppr = etree.SubElement(p, A + 'pPr', algn=algn)
    if vineta:
        ppr.set('marL', str(int(0.9 * CM)))
        ppr.set('indent', str(int(-0.9 * CM)))
    if antes:
        sb = etree.SubElement(ppr, A + 'spcBef')
        etree.SubElement(sb, A + 'spcPts', val=str(antes))
    sa = etree.SubElement(ppr, A + 'spcAft')
    etree.SubElement(sa, A + 'spcPts', val=str(despues))
    if vineta:
        etree.SubElement(ppr, A + 'buFont', typeface=FUENTE)
        etree.SubElement(ppr, A + 'buChar', char='•')
    else:
        etree.SubElement(ppr, A + 'buNone')
    for texto, negrita in partes:
        r = etree.SubElement(p, A + 'r')
        r.append(rpr(sz, negrita))
        t = etree.SubElement(r, A + 't')
        t.text = texto
    return p


def lineas(texto, sz, ancho_cm, vineta=False):
    # Arial: ancho medio de carácter ~0.5 em en texto corrido en español
    ch = sz / 100 * 0.5 * 0.03528  # cm por carácter
    util = ancho_cm - 0.5 - (0.9 if vineta else 0)
    return max(1, math.ceil(len(texto) * ch / util * 1.04))


def caja(texto_enc, x, y, w, bloques, sz=3000):
    """Encabezado verde + caja blanca. bloques: ('p', [(texto, negrita)]) | ('v', [(...)]) | ('img', ruta, alto_cm)."""
    enc = copy.deepcopy(MOLDE_ENC)
    nuevo_id(enc, 'Encabezado ' + texto_enc)
    posicion(enc, x, y, w, 2.27)
    run = enc.find('.//a:r', NS)
    run.find('a:t', NS).text = texto_enc
    rp = run.find('a:rPr', NS)
    for tag in ('latin', 'cs'):
        e = rp.find('a:' + tag, NS)
        if e is not None:
            e.set('typeface', FUENTE)
            for k in ('panose', 'pitchFamily', 'charset'):
                e.attrib.pop(k, None)
    arbol.append(enc)
    y += 2.27

    alto_linea = sz / 100 * 1.2 * 0.03528
    texto = [b for b in bloques if b[0] in ('p', 'v')]
    alto = 0.5
    cuerpo = copy.deepcopy(MOLDE_CUERPO)
    nuevo_id(cuerpo, 'Cuerpo ' + texto_enc)
    tx = cuerpo.find('.//p:txBody', NS)
    for p in tx.findall('a:p', NS):
        tx.remove(p)
    imgs = []
    for b in bloques:
        if b[0] in ('p', 'v'):
            vin = b[0] == 'v'
            tx.append(parrafo(b[1], sz, vineta=vin))
            alto += lineas(''.join(t for t, _ in b[1]), sz, w, vin) * alto_linea + 0.21
        else:
            imgs.append(b)
    posicion(cuerpo, x, y, w, alto)
    arbol.append(cuerpo)
    y += alto
    for _, ruta, alto_img in imgs:
        iw, ih = Image.open(ruta).size
        ancho_img = min(w - 1.0, alto_img * iw / ih)
        alto_img = ancho_img * ih / iw
        pic = sl.shapes.add_picture(ruta, Emu(int((x + (w - ancho_img) / 2) * CM)), Emu(int((y + 0.3) * CM)),
                                    Emu(int(ancho_img * CM)), Emu(int(alto_img * CM)))
        ln = pic.line
        ln.color.rgb = __import__('pptx').dml.color.RGBColor(0x80, 0x81, 0x85)
        ln.width = Emu(int(0.06 * CM))
        y += alto_img + 0.6
    return y + 0.7


def reescribir(nombre, parrafos, sz, interlineado=None):
    sh = buscar(nombre)
    tx = sh._element.find('.//p:txBody', NS)
    for p in tx.findall('a:p', NS):
        tx.remove(p)
    for partes, algn in parrafos:
        p = parrafo(partes, sz, algn=algn, despues=0)
        if interlineado:
            ppr = p.find('a:pPr', NS)
            ln = etree.Element(A + 'lnSpc')
            etree.SubElement(ln, A + 'spcPts', val=str(interlineado))
            ppr.insert(0, ln)
        tx.append(p)
    return sh


# ---------------------------------------------------------------- cabecera
reescribir('CuadroTexto 9', [([('HotelNova: Sistema de Gestión Hotelera con Microservicios Spring Boot, '
                                 'Scrum y Desarrollo Guiado por Especificaciones', True)], 'ctr')], 5400)
reescribir('CuadroTexto 12', [
    ([('PONENTE 1: ', True), ('JOAN FRANSSUAT MILLÁN VARGAS', True)], 'l'),
    ([('PONENTE 2: ', True), ('JAIDER JAVIER JAIMES', True)], 'l'),
    ([('DIRECTOR: ', True), ('JULIÁN BARNEY JAIMES RINCÓN', True)], 'l'),
    ([('FACULTAD: ', True), ('CIENCIAS NATURALES E INGENIERÍAS', True)], 'l'),
    ([('PROGRAMA: ', True), ('TECNOLOGÍA EN DESARROLLO DE SISTEMAS INFORMÁTICOS', True)], 'l'),
    ([('CURSO: ', True), ('D-191', True)], 'l'),
    ([('SEDE-REGIONAL: ', True), ('BUCARAMANGA', True)], 'l'),
], 3000, interlineado=4300)
reescribir('CuadroTexto 19', [
    ([('ÁREA: ', True), ('Ingeniería de software · Arquitectura de microservicios', False)], 'l'),
    ([('CATEGORÍA: ', True), ('Proyecto integrador terminado (Release 2)', False)], 'l'),
], 3400)

ANCHO = 41.26
IZQ, DER = 3.34, 46.30
B = lambda *partes: ('p', list(partes))
V = lambda *partes: ('v', list(partes))
N = lambda t: (t, True)
T = lambda t: (t, False)

# ---------------------------------------------------------------- columna izquierda
y = 24.6
y = caja('INTRODUCCIÓN', IZQ, y, ANCHO, [
    B(T('HotelNova es un sistema web para la gestión de un hotel, construido con una arquitectura de microservicios en '
        'Java 21 y Spring Boot. Cubre la estadía completa: reserva, check-in, consumo de servicios, facturación, cobro, '
        'limpieza y mantenimiento de las habitaciones, y un tablero para la gerencia.')),
    B(T('Se desarrolló en dos releases con Scrum. En el segundo se aplicó el Desarrollo Guiado por Especificaciones '
        '(SDD) y el sistema pasó de 5 a 10 roles de usuario.')),
])
y = caja('PLANTEAMIENTO DEL PROBLEMA Y JUSTIFICACIÓN', IZQ, y, ANCHO, [
    B(T('En un hotel pequeño o mediano la información de habitaciones, reservas, huéspedes y cobros suele quedar '
        'repartida en hojas de cálculo, cuadernos y mensajes. Eso produce reservas cruzadas, habitaciones que no se '
        'limpian a tiempo, cobros dobles y una gerencia sin datos para decidir.')),
    B(N('Justificación: '), T('un sistema con un módulo por área y permisos por cargo hace que cada persona vea solo lo '
        'que le toca, evita esos errores con reglas de negocio y deja registro de quién hizo qué. Los microservicios '
        'permiten que cada área crezca y se despliegue por separado.')),
    ('img', '/home/claude/work/afiche/img/portada.png', 15.5),
])
y = caja('REFERENTE TEÓRICO', IZQ, y, ANCHO, [
    B(N('Microservicios: '), T('la aplicación se divide en servicios pequeños e independientes, cada uno con su propia '
        'base de datos, que se comunican por API REST (Fowler y Lewis, 2014; Newman, 2021).')),
    B(N('Spring Boot y MongoDB: '), T('Spring Boot simplifica crear servicios web en Java; MongoDB guarda los datos como '
        'documentos, en la nube con MongoDB Atlas.')),
    B(N('Scrum y Kanban: '), T('trabajo en sprints cortos con roles, eventos y artefactos definidos (Schwaber y '
        'Sutherland, 2020), y un tablero que muestra el flujo de las tareas (Anderson, 2010).')),
    B(N('Desarrollo Guiado por Especificaciones (SDD): '), T('la especificación es el artefacto principal. Primero se '
        'escriben la constitución del proyecto, los requisitos en formato EARS (Mavin et al., 2009) con sus criterios '
        'de aceptación, el plan con decisiones de arquitectura (ADR) y el contrato OpenAPI; después las pruebas y el código.')),
    ('img', '/home/claude/work/afiche/img/arquitectura.png', 10.2),
])
y_bib = y
y = caja('BIBLIOGRAFÍA', IZQ, y, ANCHO, [
    B(T('Anderson, D. J. (2010). Kanban: Successful evolutionary change for your technology business. Blue Hole Press.')),
    B(T('Fowler, M. y Lewis, J. (2014). Microservices. https://martinfowler.com/articles/microservices.html')),
    B(T('Mavin, A., Wilkinson, P., Harwood, A. y Novak, M. (2009). Easy Approach to Requirements Syntax (EARS). '
        '17th IEEE International Requirements Engineering Conference, 317-322.')),
    B(T('Newman, S. (2021). Building microservices (2.ª ed.). O\'Reilly Media.')),
    B(T('Schwaber, K. y Sutherland, J. (2020). La Guía Scrum. https://scrumguides.org')),
    B(T('GitHub. (2025). Spec Kit: Spec-Driven Development. https://github.com/github/spec-kit')),
], sz=2400)
print('izquierda termina en', round(y, 1))

# ---------------------------------------------------------------- columna derecha
y = 16.4
y = caja('OBJETIVOS', DER, y, ANCHO, [
    B(N('Objetivo general. '), T('Desarrollar un sistema web de gestión hotelera basado en microservicios con Spring '
        'Boot y MongoDB, que atienda a 10 roles de usuario con permisos diferenciados, aplicando Scrum y el '
        'desarrollo guiado por especificaciones.')),
    B(N('Objetivos específicos.')),
    V(T('Diseñar cuatro microservicios independientes (habitaciones, reservas, huéspedes y servicios-facturación), cada '
        'uno con su base de datos y su API REST documentada con OpenAPI.')),
    V(T('Implementar una aplicación web con 10 roles en la que cada cargo vea solo sus funciones.')),
    V(T('Especificar el segundo release con SDD y mantener la trazabilidad de cada requisito hasta sus pruebas.')),
    V(T('Desplegar el sistema en la nube y validarlo con pruebas unitarias y de aceptación.')),
])
y = caja('METODOLOGÍA', DER, y, ANCHO, [
    B(T('Scrum en dos releases y ocho sprints semanales (R1-Sprint 0 a 3 y R2-Sprint 0 a 3), con 192 puntos de '
        'historia. Jaider Jaimes fue Product Owner y Joan Millán Scrum Master; los dos trabajaron como desarrolladores.')),
    B(T('Cada sprint tuvo su tablero Kanban en Trello con tarjetas de tareas, eventos e impedimentos. El avance se '
        'siguió con burndown, flujo acumulado, estado de los sprints y pruebas OK/KO.')),
    B(T('El Release 2 siguió SDD por fases con puertas de revisión: especificación, plan, tareas, pruebas primero, '
        'código y validación en Render.')),
    ('img', '/home/claude/work/afiche/img/burndown.png', 12.5),
])
y = caja('RESULTADOS', DER, y, ANCHO, [
    V(N('192 de 192 puntos terminados '), T('en los 8 sprints; el último se completó tres días antes de la fecha final.')),
    V(N('5 servicios desplegados '), T('en Render con MongoDB Atlas: hotel-web y los cuatro microservicios.')),
    V(N('10 roles '), T('con su propia pantalla; quien intenta abrir la página de otro rol vuelve a la suya.')),
    V(N('Reglas de negocio: '), T('no se cruzan reservas, una factura no se cobra dos veces, las temporadas esperan la '
        'aprobación del gerente y cada camarera ve solo sus habitaciones.')),
    V(N('Calidad: '), T('98 pruebas unitarias en verde y 34 pruebas de aceptación OK, ninguna KO. Los 26 requisitos '
        'comprometidos tienen criterio y prueba en la matriz de trazabilidad.')),
    ('img', '/home/claude/work/afiche/img/tablero.png', 15.5),
])
y = caja('CONCLUSIONES', DER, y, ANCHO, [
    B(T('Separar el sistema en microservicios permitió sumar cinco roles y reglas nuevas en el Release 2 tocando solo '
        'el servicio dueño de cada dato.')),
    B(T('Escribir primero la especificación y las pruebas hizo el código revisable y trazable: cada requisito tiene su '
        'criterio, su prueba y su tarjeta.')),
    B(T('Los tableros por sprint dejaron ver que el trabajo se concentraba los sábados; la retrospectiva lo convirtió en '
        'acciones y el último sprint arrancó desde el primer día.')),
])
print('derecha termina en', round(y, 1))
prs.save(SAL)
