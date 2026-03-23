all : PICS


######################################################################

PICS : secid-logic.png


######################################################################
# PICS

secid-logic.png : secid-logic.pdf

secid-logic.pdf : secid-logic.odg


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
