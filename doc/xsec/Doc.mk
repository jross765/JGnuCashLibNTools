all : DOC


######################################################################

DOC : PICS TEXTS

PICS : secid-logic.png

TEXTS : id-layers.pdf


######################################################################
# PICS

secid-logic.png : secid-logic.pdf

secid-logic.pdf : secid-logic.odg


######################################################################
# TEXTS

id-layers.pdf : id-layers.md \
                secid-logic.png
	pandoc -f markdown -t pdf -V geometry:a4paper -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
