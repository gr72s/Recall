import React, { useEffect, useState } from "react";
import {
  DataGrid,
  GridActionsCellItem,
  GridColDef,
  GridRowId,
  GridRowModes,
  GridRowModesModel,
  GridSlots,
  GridToolbarContainer,
  useGridApiRef
} from "@mui/x-data-grid";
import axios from "axios";
import Box from "@mui/material/Box";
import { GridPaginationModel } from "@mui/x-data-grid/models/gridPaginationProps";
import { GridCallbackDetails } from "@mui/x-data-grid/models/api";
import { GridRowModel, GridRowsProp, GridValidRowModel } from "@mui/x-data-grid/models/gridRows";
import { Snackbar } from "@mui/material";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/DeleteOutlined";
import CancelIcon from "@mui/icons-material/Close";
import SaveIcon from "@mui/icons-material/Save";


interface TagProto extends GridValidRowModel {
  id: number | null,
  identifier: string | null,
  label: string | null,
  superior: number | null,
  superiorIdentifier: string | null
}

class TagProtoImpl implements TagProto {
  constructor(public id: number | null,
              public identifier: string | null,
              public label: string | null,
              public superior: number | null,
              public superiorIdentifier: string | null) {
  }
}

interface EditToolbarProps {
  setRows: (value: (TagProto[] | ((prevState: TagProto[]) => TagProto[]))) => void;
  setRowModesModel: (newTag: (oldTag: GridRowModesModel) => GridRowModesModel) => void;
  showSnackbar: (errorMsg: string) => void;
}

const insert = (proto: TagProto): TagProto | null => {
  proto.id = null;
  console.log(proto);
  axios.post("http://localhost:8080/consume/tag/add", proto)
    .then(res => {
      console.log(res.data.data);
    });
  return null;
};

function EditToolbar(props: EditToolbarProps) {
  const setRows = props.setRows;
  const showSnackbar = props.showSnackbar;
  const setRowModesModel = props.setRowModesModel;

  const handleClick = async () => {
    let tagProto = new TagProtoImpl(-1, null, null, null, null);
    setRows((oldRows) => {
      console.log(oldRows);
      let newVar = [...oldRows, { ...tagProto, isNew: true }];
      console.log(newVar);
      return newVar;
    });
    setRowModesModel((oldTags) => ({
      ...oldTags,
      [tagProto.id!]: { mode: GridRowModes.Edit }
    }));

    // const proto = insert(tagProto);
    // if (proto != null && proto.id != null) {
    //   const id = proto.id;
    //   setRows((oldRows) => {
    //     console.log(oldRows);
    //     let newVar = [...oldRows, { ...proto, isNew: true }];
    //     console.log(newVar);
    //     return newVar;
    //   });
    //   setRowModesModel((oldTags) => ({
    //     ...oldTags,
    //     [id]: { mode: GridRowModes.Edit, fieldToFocus: "identifier" }
    //   }));
    // }
  };

  return (
    <GridToolbarContainer>
      <Button color="primary" startIcon={<AddIcon />} onClick={handleClick} />
    </GridToolbarContainer>
  );
}

const Table: React.FC = () => {

  const apiRef = useGridApiRef();

  const [tags, setTags] = useState<GridRowsProp<TagProto>>([]);
  const [paginationModel, setPaginationModel] = useState({
    pageSize: 10,
    page: 0
  });
  const [rowCount, setRowCount] = useState<number>(0);
  const [tagsModesModel, setTagsModesModel] = React.useState<GridRowModesModel>({});


  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMsg, setSnackbarMsg] = useState<string>("");

  const fetch = (page: number = 0, size: number = 10, ignore: boolean = false) => {
    ++page;
    axios.get(`http://localhost:8080/consume/tag?page=${page}&size=${size}`)
      .then(res => {
        if (!ignore) {
          setTags(res.data.data.content);
          setRowCount(res.data.data.totalSize);
        }
      })
      .catch(err => {
        showSnackbar(err.toString());
      });
  };

  useEffect(() => {
    let ignore = false;
    fetch(paginationModel.page, paginationModel.pageSize, ignore);
    return () => {
      ignore = true;
    };
  }, []);

  const handlePageChange = (model: GridPaginationModel, details: GridCallbackDetails) => {
    fetch(model.page, model.pageSize);
    setPaginationModel(model);
  };

  const handleRowUpdate = (newRow: GridRowModel<TagProto>, oldRow: GridRowModel<TagProto>) => {

    const update = (data: TagProto) => {
      axios.post(`http://localhost:8080/consume/tag/update`, data)
        .then(res => {
          if (res.data.status !== 200) {
            showSnackbar(res.data.error.reason.toString());
          } else {
            setTags(tags.map(tag => {
              if (tag.id === res.data.data.id) {
                return { ...tag, ...res.data.data };
              }
              return tag;
            }));
          }
        })
        .catch(err => showSnackbar(err.toString()));
    };
    update(newRow);
    return newRow;
  };

  const handleCloseSnackbar = () => setSnackbarOpen(false);

  const showSnackbar = (msg: string) => {
    setSnackbarMsg(msg);
    setSnackbarOpen(true);
  };

  const handleSaveClick = (id: GridRowId) => () => {
    apiRef.current.stopRowEditMode({ id: id });
  };

  const handleCancelClick = (id: GridRowId) => () => {
    apiRef.current.stopRowEditMode({ id: id, ignoreModifications: true });
  };

  const rowModesModelChangeHandle = (rowModesModel: GridRowModesModel, details: GridCallbackDetails) => {
    setTagsModesModel(rowModesModel);
  };

  const handleEditClick = (id: GridRowId) => () => {
    apiRef.current.startRowEditMode({ id: id });
  };

  const handleDeleteClick = (id: GridRowId) => () => {
    const proto = apiRef.current.getRow<TagProto>(id);
    const del = (datas: TagProto[]) => {
      axios.post(`http://localhost:8080/consume/tag/del`, datas)
        .then(res => {
          if (res.data.status !== 200) {
            showSnackbar(res.data.error.reason.toString());
          } else {
            setTags(tags.map(tag => {
              if (tag.id === res.data.data.id) {
                return { ...tag, ...res.data.data };
              }
              return tag;
            }));
          }
        })
        .catch(err => showSnackbar(err.toString()));
    };
    if (proto != null) {
      del([proto]);
    }
  };

  const columns: GridColDef[] = [
    { field: "id", headerName: "ID", editable: true },
    { field: "identifier", headerName: "标识", editable: true },
    { field: "label", headerName: "名称", editable: true },
    { field: "superior", headerName: "父级ID", editable: true },
    { field: "superiorIdentifier", headerName: "父级标识", editable: true },
    {
      field: "Actions", headerName: "编辑", type: "actions", cellClassName: "actions",
      getActions: ({ id }) => {
        const isInEditMode = tagsModesModel[id]?.mode === GridRowModes.Edit;
        if (isInEditMode) {
          return [
            <GridActionsCellItem
              icon={<SaveIcon />}
              label="Save"
              sx={{
                color: "primary.main"
              }}
              onClick={handleSaveClick(id)}
            />,
            <GridActionsCellItem
              icon={<CancelIcon />}
              label="Cancel"
              className="textPrimary"
              onClick={handleCancelClick(id)}
              color="inherit"
            />
          ];
        }
        return [
          <GridActionsCellItem
            icon={<EditIcon />}
            label="Edit"
            className="textPrimary"
            onClick={handleEditClick(id)}
            color="inherit"
          />,
          <GridActionsCellItem
            label="Delete"
            icon={<DeleteIcon />}
            className="textPrimary"
            onClick={handleDeleteClick(id)}
            color="inherit"
          />
        ];
      }
    }
  ];

  return <Box
    sx={{
      height: 900,
      width: "100%",
      "& .actions": {
        color: "text.secondary"
      },
      "& .textPrimary": {
        color: "text.primary"
      }
    }}
  >
    <DataGrid
      apiRef={apiRef}
      rows={tags}
      columns={columns}
      editMode={"row"}
      onRowModesModelChange={rowModesModelChangeHandle}
      paginationModel={paginationModel}
      paginationMode="server"
      onPaginationModelChange={handlePageChange}
      pageSizeOptions={[10, 20, 50]}
      rowCount={rowCount}
      onRowCountChange={setRowCount}
      processRowUpdate={handleRowUpdate}
      slots={{
        toolbar: EditToolbar as GridSlots["toolbar"]
      }}
      slotProps={{
        toolbar: { setRows: setTags, setRowModesModel: setTagsModesModel, showSnackbar } as EditToolbarProps
      }}
    />
    <Snackbar
      open={snackbarOpen}
      autoHideDuration={3000}
      onClose={handleCloseSnackbar}
      message={snackbarMsg}
    />
  </Box>;
};

export default Table;