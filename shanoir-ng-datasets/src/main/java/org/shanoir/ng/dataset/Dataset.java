package org.shanoir.ng.dataset;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.shanoir.ng.dataset.modality.CalibrationDataset;
import org.shanoir.ng.dataset.modality.CtDataset;
import org.shanoir.ng.dataset.modality.EegDataset;
import org.shanoir.ng.dataset.modality.MegDataset;
import org.shanoir.ng.dataset.modality.MeshDataset;
import org.shanoir.ng.dataset.modality.MrDataset;
import org.shanoir.ng.dataset.modality.ParameterQuantificationDataset;
import org.shanoir.ng.dataset.modality.PetDataset;
import org.shanoir.ng.dataset.modality.RegistrationDataset;
import org.shanoir.ng.dataset.modality.SegmentationDataset;
import org.shanoir.ng.dataset.modality.SpectDataset;
import org.shanoir.ng.dataset.modality.StatisticalDataset;
import org.shanoir.ng.dataset.modality.TemplateDataset;
import org.shanoir.ng.datasetacquisition.DatasetAcquisition;
import org.shanoir.ng.processing.DatasetProcessing;
import org.shanoir.ng.processing.InputOfDatasetProcessing;
import org.shanoir.ng.shared.model.AbstractGenericItem;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Dataset.
 * 
 * @author msimon
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = CalibrationDataset.class, name = "Calibration"),
		@JsonSubTypes.Type(value = CtDataset.class, name = "Ct"),
		@JsonSubTypes.Type(value = EegDataset.class, name = "Eeg"),
		@JsonSubTypes.Type(value = MegDataset.class, name = "Meg"),
		@JsonSubTypes.Type(value = MeshDataset.class, name = "Mesh"),
		@JsonSubTypes.Type(value = MrDataset.class, name = "Mr"),
		@JsonSubTypes.Type(value = ParameterQuantificationDataset.class, name = "ParameterQuantification"),
		@JsonSubTypes.Type(value = PetDataset.class, name = "Pet"),
		@JsonSubTypes.Type(value = RegistrationDataset.class, name = "Registration"),
		@JsonSubTypes.Type(value = SegmentationDataset.class, name = "Segmentation"),
		@JsonSubTypes.Type(value = SpectDataset.class, name = "Spect"),
		@JsonSubTypes.Type(value = StatisticalDataset.class, name = "Statistical"),
		@JsonSubTypes.Type(value = TemplateDataset.class, name = "Template") })
public abstract class Dataset extends AbstractGenericItem {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6712556010816448026L;

	/**
	 * Flag to indicate whether this dataset is related to a single subject or
	 * to multiple subjects.
	 */
	@NotNull
	private Integer cardinalityOfRelatedSubjects;

	/**
	 * A comment on the dataset. In case of importing from dicom files, it could
	 * be the series description for instance.
	 */
	private String comment;

	/** Creation date of the dataset. */
	private Date creationDate;

	/** Dataset Acquisition. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dataset_acquisition_id")
	private DatasetAcquisition datasetAcquisition;

	/** Dataset expression list. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataset", cascade = CascadeType.ALL)
	private List<DatasetExpression> datasetExpressions;

	/** Dataset Modality Type. */
	private Integer datasetModalityType;

	/** Dataset Processing. */
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "dataset_aprocessing_id")
	private DatasetProcessing datasetProcessing;

	/** Explored entity. */
	private Integer exploredEntity;

	/**
	 * Group of subjects. Constraint: not null if dataset.subject == null and
	 * null if dataset.subject != null.
	 */
	private Long groupOfSubjectsId;

	/** Relations between the datasets and the dataset processing (input). */
	@OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InputOfDatasetProcessing> inputOfDatasetProcessings;

	/**
	 * The name of this dataset. For instance, it could be 'BrainWeb',
	 * 'ICBM152', 'T1-weighted High resolution image without injection' etc.
	 */
	private String name;

	/** Processed dataset type. */
	private Integer processedDatasetType;

	/**
	 * Parent dataset with the same sampling grid, ie that can be superimposed
	 * with this dataset.
	 */
	@ManyToOne
	@JoinColumn(name = "referenced_dataset_for_superimposition_id")
	private Dataset referencedDatasetForSuperimposition;

	/**
	 * List of children datasets with the same sampling grid, ie that can be
	 * superimposed with this dataset.
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "referencedDatasetForSuperimposition", cascade = CascadeType.ALL)
	private List<Dataset> referencedDatasetForSuperimpositionChildrenList;

	/** The study for which this dataset is a result. */
	private Long studyId;

	/** Subject. */
	private Long subjectId;

	/**
	 * @return the cardinalityOfRelatedSubjects
	 */
	public CardinalityOfRelatedSubjects getCardinalityOfRelatedSubjects() {
		return CardinalityOfRelatedSubjects.getCardinality(cardinalityOfRelatedSubjects);
	}

	/**
	 * @param cardinalityOfRelatedSubjects
	 *            the cardinalityOfRelatedSubjects to set
	 */
	public void setCardinalityOfRelatedSubjects(CardinalityOfRelatedSubjects cardinalityOfRelatedSubjects) {
		if (cardinalityOfRelatedSubjects == null) {
			this.cardinalityOfRelatedSubjects = null;
		} else {
			this.cardinalityOfRelatedSubjects = cardinalityOfRelatedSubjects.getId();
		}
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the datasetAcquisition
	 */
	public DatasetAcquisition getDatasetAcquisition() {
		return datasetAcquisition;
	}

	/**
	 * @param datasetAcquisition
	 *            the datasetAcquisition to set
	 */
	public void setDatasetAcquisition(DatasetAcquisition datasetAcquisition) {
		this.datasetAcquisition = datasetAcquisition;
	}

	/**
	 * @return the datasetExpressions
	 */
	public List<DatasetExpression> getDatasetExpressions() {
		return datasetExpressions;
	}

	/**
	 * @param datasetExpressions
	 *            the datasetExpressions to set
	 */
	public void setDatasetExpressions(List<DatasetExpression> datasetExpressions) {
		this.datasetExpressions = datasetExpressions;
	}

	/**
	 * @return the datasetModalityType
	 */
	public DatasetModalityType getDatasetModalityType() {
		return DatasetModalityType.getType(datasetModalityType);
	}

	/**
	 * @param datasetModalityType
	 *            the datasetModalityType to set
	 */
	public void setDatasetModalityType(DatasetModalityType datasetModalityType) {
		if (datasetModalityType == null) {
			this.datasetModalityType = null;
		} else {
			this.datasetModalityType = datasetModalityType.getId();
		}
	}

	/**
	 * @return the datasetProcessing
	 */
	public DatasetProcessing getDatasetProcessing() {
		return datasetProcessing;
	}

	/**
	 * @param datasetProcessing
	 *            the datasetProcessing to set
	 */
	public void setDatasetProcessing(DatasetProcessing datasetProcessing) {
		this.datasetProcessing = datasetProcessing;
	}

	/**
	 * @return the exploredEntity
	 */
	public ExploredEntity getExploredEntity() {
		return ExploredEntity.getEntity(exploredEntity);
	}

	/**
	 * @param exploredEntity
	 *            the exploredEntity to set
	 */
	public void setExploredEntity(ExploredEntity exploredEntity) {
		if (exploredEntity == null) {
			this.exploredEntity = null;
		} else {
			this.exploredEntity = exploredEntity.getId();
		}
	}

	/**
	 * @return the groupOfSubjectsId
	 */
	public Long getGroupOfSubjectsId() {
		return groupOfSubjectsId;
	}

	/**
	 * @param groupOfSubjectsId
	 *            the groupOfSubjectsId to set
	 */
	public void setGroupOfSubjectsId(Long groupOfSubjectsId) {
		this.groupOfSubjectsId = groupOfSubjectsId;
	}

	/**
	 * @return the inputOfDatasetProcessings
	 */
	public List<InputOfDatasetProcessing> getInputOfDatasetProcessings() {
		return inputOfDatasetProcessings;
	}

	/**
	 * @param inputOfDatasetProcessings
	 *            the inputOfDatasetProcessings to set
	 */
	public void setInputOfDatasetProcessings(List<InputOfDatasetProcessing> inputOfDatasetProcessings) {
		this.inputOfDatasetProcessings = inputOfDatasetProcessings;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the processedDatasetType
	 */
	public ProcessedDatasetType getProcessedDatasetType() {
		return ProcessedDatasetType.getType(processedDatasetType);
	}

	/**
	 * @param processedDatasetType
	 *            the processedDatasetType to set
	 */
	public void setProcessedDatasetType(ProcessedDatasetType processedDatasetType) {
		if (processedDatasetType == null) {
			this.processedDatasetType = null;
		} else {
			this.processedDatasetType = processedDatasetType.getId();
		}
	}

	/**
	 * @return the referencedDatasetForSuperimposition
	 */
	public Dataset getReferencedDatasetForSuperimposition() {
		return referencedDatasetForSuperimposition;
	}

	/**
	 * @param referencedDatasetForSuperimposition
	 *            the referencedDatasetForSuperimposition to set
	 */
	public void setReferencedDatasetForSuperimposition(Dataset referencedDatasetForSuperimposition) {
		this.referencedDatasetForSuperimposition = referencedDatasetForSuperimposition;
	}

	/**
	 * @return the referencedDatasetForSuperimpositionChildrenList
	 */
	public List<Dataset> getReferencedDatasetForSuperimpositionChildrenList() {
		return referencedDatasetForSuperimpositionChildrenList;
	}

	/**
	 * @param referencedDatasetForSuperimpositionChildrenList
	 *            the referencedDatasetForSuperimpositionChildrenList to set
	 */
	public void setReferencedDatasetForSuperimpositionChildrenList(
			List<Dataset> referencedDatasetForSuperimpositionChildrenList) {
		this.referencedDatasetForSuperimpositionChildrenList = referencedDatasetForSuperimpositionChildrenList;
	}

	/**
	 * @return the studyId
	 */
	public Long getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            the studyId to set
	 */
	public void setStudyId(Long studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return the subjectId
	 */
	public Long getSubjectId() {
		return subjectId;
	}

	/**
	 * @param subjectId
	 *            the subjectId to set
	 */
	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Transient
	public abstract String getType();

}
