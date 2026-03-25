all : TEXTS


######################################################################

TEXTS : select-securities.pdf


######################################################################
# TEXTS

select-securities.pdf : select-securities.md \
                        ../xsec/secid-logic.png
	pandoc -f markdown -t pdf -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
